package io.anuke.mindustry.masm;

public class MasmExecutor{
    /** All instructions to execute. */
    public int[] instructions;
    /** Register values. */
    public int[] registers = new int[9];
    /** Program counter. */
    public int pcounter;
    /** Memory bytes. */
    public byte[] memory;

    public MasmExecutor(int[] instructions, int totalMemory){
        this.instructions = instructions;
        this.memory = new byte[totalMemory];
    }

    /** Executes a single instruction. */
    public void execute(){
        int is = instructions[pcounter];


        pcounter ++;
    }
}
