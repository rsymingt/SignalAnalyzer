import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import WavFile.WavFile;

import fourier.*;

public class SignalAnalyzer {

   public SignalAnalyzer()
   {
   }

   public AudioInfo getInfo(String file, int L)
   {
      AudioInfo audioInfo = new AudioInfo(file, L);
      try {
         // Open the wav file specified as the first argument
         WavFile wavFile = WavFile.openWavFile(new File(file));

         // Display information about the wav file
         // wavFile.display();

         // Get the number of audio channels in the wav file
         int numChannels = wavFile.getNumChannels();
         long sampleRate = wavFile.getSampleRate();
         int numWindows = (int) Math.ceil((wavFile.getNumFrames() / (double) L));

         audioInfo.numChannels = numChannels;
         audioInfo.sampleRate = sampleRate;
         audioInfo.totalFrames = wavFile.getNumFrames();

         // Create a buffer of 100 frames
         double[][] buffer = new double[numChannels][L];
         audioInfo.frames = new double[numWindows][numChannels][L];

         int framesRead;

         // System.out.println("Number of Windows: " + numWindows);

         int offset = 0;
         do {
            // Read frames into buffer
            framesRead = wavFile.readFrames(buffer, L);

            // Loop through frames and look for minimum and maximum value
            for (int k = 0; k < L/2; k++) {

               for (int c = 0; c < numChannels; c++) // should be num channels
               {
                  for (int s = 0; s < framesRead; s++) {
                     // if (buffer[c][s] > max)
                     //    max = buffer[c][s];
                     // if (buffer[c][s] < min)
                     //    min = buffer[c][s];
                     audioInfo.frames[offset][c][s] = buffer[c][s];
                  }
               }

            }

            offset++;
         } while (framesRead != 0);

      }catch(Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      return audioInfo;
   }

   public STFT getSTFT(AudioInfo audioInfo)
   {
      STFT stft = new STFT();

      double[][][] frames = audioInfo.frames;

      int L = audioInfo.L;

      for(int m = 0; m < frames.length; m ++)
      {
         FT ft = new FT();

         for (int k = 0; k < L/2; k++) 
         {
            double realCoef = 0;
            double complexCoef = 0;

            for(int c = 1; c < frames[m].length; c ++) // reading only 2nd channel currently
            {
               for(int s = 0; s < L; s ++)
               {
                  realCoef += (frames[m][c][s]) * Math.cos(((2 * Math.PI) / L) * s * k);
                  complexCoef += (frames[m][c][s]) * -Math.sin( ((2*Math.PI) / L) * s * k );
               }
            }

            ft.real_coefs.add(realCoef);
            ft.complex_coefs.add(complexCoef);
         }

         stft.add(ft);
      }

      return stft;
   }

   public static void main(String[] args) {

      // Scanner scanner = new Scanner(System.in);

      // String file = scanner.nextLine();

      // scanner.close();

      SignalAnalyzer sa = new SignalAnalyzer();

      AudioInfo audioInfo = sa.getInfo("files/dtmf 123.wav", 512);
      STFT stft = sa.getSTFT(audioInfo);

      int L = audioInfo.L;
      long sampleRate = audioInfo.sampleRate;

      double sliceWidth = ((double)L)/((double)sampleRate);
      double maxPosFreq = sampleRate/2;

      double freqResolution = sampleRate/L;

      System.out.println(stft.ft_list.size());

      for(int m = 0; m < stft.ft_list.size(); m ++) // time
      {
         FT ft = stft.ft_list.get(m);

         double maxPower = 0;
         double maxFreq = 0;
         double maxTime = 0;
         for(int k = 0; k < L/2; k ++) // freq
         {
            double realCoef = ft.real_coefs.get(k);

            double power = 10*Math.log10(Math.abs(realCoef));

            double time = m * sliceWidth;
            double currentFreq = maxPosFreq * ((double)k / (double)(L/2) );

            if(power > maxPower)
            {
               maxFreq = currentFreq;
               maxTime = time;
               maxPower = power;
            }

         }

         if(maxPower > 0)
         {
            System.out.format("time: %.2fs = f:%.2f kHz, p:%.2f DB\n" , maxTime, maxFreq, maxPower);
         }

      }

      System.out.format("resolution: %.2f Hz\n", freqResolution);

      // String file = "files/dtmf 123.wav";

      // try {
      //    // Open the wav file specified as the first argument
      //    WavFile wavFile = WavFile.openWavFile(new File(file));

      //    // Display information about the wav file
      //    wavFile.display();

      //    // Get the number of audio channels in the wav file
      //    int numChannels = wavFile.getNumChannels();
      //    long sampleRate = wavFile.getSampleRate();

      //    // Create a buffer of 100 frames
      //    double[][] buffer = new double[numChannels][L];

      //    int framesRead;
      //    double min = Double.MAX_VALUE;
      //    double max = Double.MIN_VALUE;

      //    int numWindows = (int) Math.ceil((wavFile.getNumFrames() / (double) L));

      //    System.out.println("Number of Windows: " + numWindows);

      //    STFT stft = new STFT();

      //    do {
      //       // Read frames into buffer
      //       framesRead = wavFile.readFrames(buffer, L);

      //       FT ft = new FT();

      //       // Loop through frames and look for minimum and maximum value
      //       for (int k = 0; k < L/2; k++) {

      //          double realCoef = 0;
      //          double complexCoef = 0;

      //          for (int c = 0; c < 1; c++) // should be num channels
      //          {
      //             for (int s = 0; s < framesRead; s++) {
      //                if (buffer[c][s] > max)
      //                   max = buffer[c][s];
      //                if (buffer[c][s] < min)
      //                   min = buffer[c][s];

      //                // sum all points mult with exp(-j * 2*PI/L * n * k) for both complex and real
      //                realCoef += (buffer[c][s]) * Math.cos(((2 * Math.PI) / L) * s * k);
      //                complexCoef += (buffer[c][s]) * -Math.sin( ((2*Math.PI) / L) * s * k );
      //             }
      //          }

      //          ft.real_coefs.add(realCoef);
      //          ft.complex_coefs.add(complexCoef);

      //       }

      //       stft.add(ft);
      //    } while (framesRead != 0);

      //    // Close the wavFile
      //    wavFile.close();

      //    // Output the minimum and maximum value
      //    System.out.printf("Min: %f, Max: %f\n", min, max);

      //    double sliceWidth = ((double)L)/((double)sampleRate);
      //    double maxPosFreq = sampleRate/2;

      //    double freqResolution = sampleRate/L;

      //    for(int m = 0; m < stft.ft_list.size(); m ++) // time
      //    {
      //       FT ft = stft.ft_list.get(m);

      //       double maxPower = 0;
      //       double maxFreq = 0;
      //       double maxTime = 0;
      //       for(int k = 0; k < L/2; k ++) // freq
      //       {
      //          double realCoef = ft.real_coefs.get(k);

      //          double power = 10*Math.log10(Math.abs(realCoef));

      //          double time = m * sliceWidth;
      //          double currentFreq = maxPosFreq * ((double)k / (double)(L/2) );

      //          if(power > maxPower)
      //          {
      //             maxFreq = currentFreq;
      //             maxTime = time;
      //             maxPower = power;
      //          }

      //       }

      //       if(maxPower > 0)
      //       {
      //          System.out.format("time: %.2fs = f:%.2f kHz, p:%.2f DB\n" , maxTime, maxFreq, maxPower);
      //       }

      //    }

      //    System.out.format("resolution: %.2f Hz\n", freqResolution);

      // }
      // catch (Exception e)
      // {
      //    System.err.println(e);
      // }
   }
}