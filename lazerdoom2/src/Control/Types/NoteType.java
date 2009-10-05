package Control.Types;

import java.util.Random;

public class NoteType extends DoubleType {
	// maybe support different scales lateron
	public static double noteArray[] = new double[] {         8.17579891564 ,
        8.66195721803 ,
        9.17702399742 ,
        9.72271824132 ,
        10.3008611535 ,
        10.9133822323 ,
        11.5623257097 ,
        12.2498573744 ,
        12.9782717994 ,
        13.75 ,
        14.5676175474 ,
        15.4338531643 ,
        16.3515978313 ,
        17.3239144361 ,
        18.3540479948 ,
        19.4454364826 ,
        20.6017223071 ,
        21.8267644646 ,
        23.1246514195 ,
        24.4997147489 ,
        25.9565435987 ,
        27.5 ,
        29.1352350949 ,
        30.8677063285 ,
        32.7031956626 ,
        34.6478288721 ,
        36.7080959897 ,
        38.8908729653 ,
        41.2034446141 ,
        43.6535289291 ,
        46.249302839 ,
        48.9994294977 ,
        51.9130871975 ,
        55.0 ,
        58.2704701898 ,
        61.735412657 ,
        65.4063913251 ,
        69.2956577442 ,
        73.4161919794 ,
        77.7817459305 ,
        82.4068892282 ,
        87.3070578583 ,
        92.4986056779 ,
        97.9988589954 ,
        103.826174395 ,
        110.0 ,
        116.54094038 ,
        123.470825314 ,
        130.81278265 ,
        138.591315488 ,
        146.832383959 ,
        155.563491861 ,
        164.813778456 ,
        174.614115717 ,
        184.997211356 ,
        195.997717991 ,
        207.65234879 ,
        220.0 ,
        233.081880759 ,
        246.941650628 ,
        261.625565301 ,
        277.182630977 ,
        293.664767917 ,
        311.126983722 ,
        329.627556913 ,
        349.228231433 ,
        369.994422712 ,
        391.995435982 ,
        415.30469758 ,
        440.0 ,
        466.163761518 ,
        493.883301256 ,
        523.251130601 ,
        554.365261954 ,
        587.329535835 ,
        622.253967444 ,
        659.255113826 ,
        698.456462866 ,
        739.988845423 ,
        783.990871963 ,
        830.60939516 ,
        880.0 ,
        932.327523036 ,
        987.766602512 ,
        1046.5022612 ,
        1108.73052391 ,
        1174.65907167 ,
        1244.50793489 ,
        1318.51022765 ,
        1396.91292573 ,
        1479.97769085 ,
        1567.98174393 ,
        1661.21879032 ,
        1760.0 ,
        1864.65504607 ,
        1975.53320502 ,
        2093.0045224 ,
        2217.46104781 ,
        2349.31814334 ,
        2489.01586978 ,
        2637.0204553 ,
        2793.82585146 ,
        2959.95538169 ,
        3135.96348785 ,
        3322.43758064 ,
        3520.0 ,
        3729.31009214 ,
        3951.06641005 ,
        4186.00904481 ,
        4434.92209563 ,
        4698.63628668 ,
        4978.03173955 ,
        5274.04091061 ,
        5587.65170293 ,
        5919.91076339 ,
        6271.92697571 ,
        6644.87516128 ,
        7040.0 ,
        7458.62018429 ,
        7902.1328201 ,
        8372.01808962 ,
        8869.84419126 ,
        9397.27257336 ,
        9956.06347911 ,
        10548.0818212 ,
        11175.3034059 ,
        11839.8215268
};
public static String noteNameArray[] = new String[] {
        "C-1" ,
        "C#-1" ,
        "D-1" ,
        "D#-1" ,
        "E-1" ,
        "F-1" ,
        "F#-1" ,
        "G-1" ,
        "G#-1" ,
        "A-1" ,
        "A#-1" ,
        "B-1" ,
        "C0" ,
        "C#0" ,
        "D0" ,
        "D#0" ,
        "E0" ,
        "F0" ,
        "F#0" ,
        "G0" ,
        "G#0" ,
        "A0" ,
        "A#0" ,
        "B0" ,
        "C1" ,
        "C#1" ,
        "D1" ,
        "D#1" ,
        "E1" ,
        "F1" ,
        "F#1" ,
        "G1" ,
        "G#1" ,
        "A1" ,
        "A#1" ,
        "B1" ,
        "C2" ,
        "C#2" ,
        "D2" ,
        "D#2" ,
        "E2" ,
        "F2" ,
        "F#2" ,
        "G2" ,
        "G#2" ,
        "A2" ,
        "A#2" ,
        "B2" ,
        "C3" ,
        "C#3" ,
        "D3" ,
        "D#3" ,
        "E3" ,
        "F3" ,
        "F#3" ,
        "G3" ,
        "G#3" ,
        "A3" ,
        "A#3" ,
        "B3" ,
        "C4" ,
        "C#4" ,
        "D4" ,
        "D#4" ,
        "E4" ,
        "F4" ,
        "F#4" ,
        "G4" ,
        "G#4" ,
        "A4" ,
        "A#4" ,
        "B4" ,
        "C5" ,
        "C#5" ,
        "D5" ,
        "D#5" ,
        "E5" ,
        "F5" ,
        "F#5" ,
        "G5" ,
        "G#5" ,
        "A5" ,
        "A#5" ,
        "B5" ,
        "C6" ,
        "C#6" ,
        "D6" ,
        "D#6" ,
        "E6" ,
        "F6" ,
        "F#6" ,
        "G6" ,
        "G#6" ,
        "A6" ,
        "A#6" ,
        "B6" ,
        "C7" ,
        "C#7" ,
        "D7" ,
        "D#7" ,
        "E7" ,
        "F7" ,
        "F#7" ,
        "G7" ,
        "G#7" ,
        "A7" ,
        "A#7" ,
        "B7" ,
        "C8" ,
        "C#8" ,
        "D8" ,
        "D#8" ,
        "E8" ,
        "F8" ,
        "F#8" ,
        "G8" ,
        "G#8" ,
        "A8" ,
        "A#8" ,
        "B8" ,
        "C9" ,
        "C#9" ,
        "D9" ,
        "D#9" ,
        "E9" ,
        "F9" ,
        "F#9"
};
public static enum NoteIndex {
        C_1 ,
        CSharp_1 ,
        D_1 ,
        DSharp_1 ,
        E_1 ,
        F_1 ,
        FSharp_1 ,
        G_1 ,
        GSharp_1 ,
        A_1 ,
        ASharp_1 ,
        B_1 ,
        C0 ,
        CSharp0 ,
        D0 ,
        DSharp0 ,
        E0 ,
        F0 ,
        FSharp0 ,
        G0 ,
        GSharp0 ,
        A0 ,
        ASharp0 ,
        B0 ,
        C1 ,
        CSharp1 ,
        D1 ,
        DSharp1 ,
        E1 ,
        F1 ,
        FSharp1 ,
        G1 ,
        GSharp1 ,
        A1 ,
        ASharp1 ,
        B1 ,
        C2 ,
        CSharp2 ,
        D2 ,
        DSharp2 ,
        E2 ,
        F2 ,
        FSharp2 ,
        G2 ,
        GSharp2 ,
        A2 ,
        ASharp2 ,
        B2 ,
        C3 ,
        CSharp3 ,
        D3 ,
        DSharp3 ,
        E3 ,
        F3 ,
        FSharp3 ,
        G3 ,
        GSharp3 ,
        A3 ,
        ASharp3 ,
        B3 ,
        C4 ,
        CSharp4 ,
        D4 ,
        DSharp4 ,
        E4 ,
        F4 ,
        FSharp4 ,
        G4 ,
        GSharp4 ,
        A4 ,
        ASharp4 ,
        B4 ,
        C5 ,
        CSharp5 ,
        D5 ,
        DSharp5 ,
        E5 ,
        F5 ,
        FSharp5 ,
        G5 ,
        GSharp5 ,
        A5 ,
        ASharp5 ,
        B5 ,
        C6 ,
        CSharp6 ,
        D6 ,
        DSharp6 ,
        E6 ,
        F6 ,
        FSharp6 ,
        G6 ,
        GSharp6 ,
        A6 ,
        ASharp6 ,
        B6 ,
        C7 ,
        CSharp7 ,
        D7 ,
        DSharp7 ,
        E7 ,
        F7 ,
        FSharp7 ,
        G7 ,
        GSharp7 ,
        A7 ,
        ASharp7 ,
        B7 ,
        C8 ,
        CSharp8 ,
        D8 ,
        DSharp8 ,
        E8 ,
        F8 ,
        FSharp8 ,
        G8 ,
        GSharp8 ,
        A8 ,
        ASharp8 ,
        B8 ,
        C9 ,
        CSharp9 ,
        D9 ,
        DSharp9 ,
        E9 ,
        F9 ,
        FSharp9
	};

	public static int noteScaleSize = NoteType.noteArray.length;
	private float gateValue = 0.0f;
	private int noteNumber = 0;
	private boolean isNoteOff = false;
	private NoteType noteOff = null;
	
	public NoteType(int noteNumber, float gate) {
		super(NoteType.noteArray[noteNumber]);
		this.noteNumber = noteNumber;
		this.gateValue = gate;
	}
	
	public int getNoteNumber() {
		return this.noteNumber;
	}
	
	private long length = 0;
	public void setLength(long length) {
		this.length = length;
	}
	public long getLength() {
		return this.length;
	}
	
	public void setIsNoteOff() {
		this.isNoteOff = true;
	}
	
	public boolean isNoteOff() {
		return this.isNoteOff;
	}
	
	public void setNoteOff(NoteType noteOff) {
		this.noteOff = noteOff;
	}
	
	public NoteType getNoteOff() {
		return this.noteOff;
	}
	
	@Override
	public float getFloatValue2() {
		return this.gateValue;
	}
	
	public float getGateValue() {
		return this.gateValue;
	}

}
