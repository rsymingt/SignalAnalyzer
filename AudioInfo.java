
public class AudioInfo
{

    public String filename;

    public long sampleRate, totalFrames;
    public int numChannels, L;

    public double[][][] frames; // not total split by L

    public AudioInfo(String filename, int L)
    {
        this.filename = filename;
        this.L = L;
    }
}