
package fourier;

import java.util.ArrayList;

public class STFT
{

    public ArrayList<FT> ft_list;

    public STFT()
    {
        ft_list = new ArrayList<FT>();
    }

    public void add(FT ft)
    {
        ft_list.add(ft);
    }

}