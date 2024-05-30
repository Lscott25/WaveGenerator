import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SineGenerator {
    //
    protected static final int SAMPLE_RATE = 16 * 1024;

    private int freq; // frequency of carrier
    private double amplitude;// total amplitude of carrier
    private boolean inclAng;  // boolean for including angle in modulator wave equation
    private double modAmp; // amplitude of the modulating wave
    private int numMod; // number of modulating waves
    private int numTime; // time in seconds

    public SineGenerator(int freq, double amplitude, boolean incAng, double modAmp, int numMod, int numTime) {
        this.freq = freq; 
        this.amplitude = amplitude;
        this.inclAng = incAng;  
        this.modAmp = modAmp; 
        this.numMod = numMod; 
        this.numTime = numTime;
    }
    
    public byte[] createSinWaveBuffer() {
        int ms = numTime * 1000;
        int samples = (int)((ms * SAMPLE_RATE) / 1000);
        byte[] output = new byte[samples];

        double period = (double)SAMPLE_RATE / freq;

        for (int i = 0; i < output.length; i++) {
            double angle = 2.0 * Math.PI * i / (SAMPLE_RATE / (freq));

            if (modAmp <= 0) { // if the modulator ampliltude is 0, then the unaffected carrier wave is played
                output[i] = (byte)(Math.sin(angle) * amplitude); 
            }
            else {
                output[i] = (byte)(Math.sin(recur(modAmp, angle, numMod, inclAng)) * 127f); 
            }
        }

        return output;
    }
        




    public void play() throws LineUnavailableException {
        final AudioFormat af= new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
        SourceDataLine line = AudioSystem.getSourceDataLine(af);
        line.open(af, SAMPLE_RATE);
        line.start();

        byte [] toneBuffer = createSinWaveBuffer();
 
        line.write(toneBuffer, 0, toneBuffer.length);

        line.drain();
        line.close();
    }


    public static double recur(double amplitude, double angle, int num, boolean inclAng) {
        if (num<=1) {
            if (inclAng)
                return angle + amplitude*Math.sin(angle);
            else 
                return amplitude*Math.sin(angle);
        }
        else {
            if (inclAng)
                return angle + amplitude*Math.sin(recur(amplitude, angle, num-1, inclAng));
            else   
                return amplitude*Math.sin(recur(amplitude, angle, num-1, inclAng));
        }
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public void setInclAng(boolean inclAng) {
        this.inclAng = inclAng;
    }

    public void setModAmp(double modAmp) {
        this.modAmp = modAmp;
    }

    public void setNumMod(int numMod) {
        this.numMod = numMod;
    }

    public void setNumTime(int numTime) {
        this.numTime = numTime;
    }
}
