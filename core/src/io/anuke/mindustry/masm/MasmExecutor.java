package io.anuke.mindustry.masm;

import java.nio.*;

public class MasmExecutor{
    /** All instructions to execute. */
    public int[] instructions;
    /** Register values. */
    public int[] registers = new int[9];
    /** Program counter. */
    public int pcounter;
    /** Memory bytes. */
    public ByteBuffer memory;

    public MasmExecutor(int[] instructions, int totalMemory){
        this.instructions = instructions;
        this.memory = ByteBuffer.allocate(totalMemory);
    }

    /** Executes a single instruction. */
    public void execute(){
        int is = instructions[pcounter];


        pcounter ++;
    }

    public enum MasmInstruction{
        add,
        and,
    }
}
