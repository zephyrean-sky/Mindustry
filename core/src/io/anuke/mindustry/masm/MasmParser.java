package io.anuke.mindustry.masm;

import io.anuke.arc.collection.*;
import io.anuke.mindustry.*;

import java.io.*;
import java.util.*;

public class MasmParser{

    public int[] parse(String input) throws MasmParseException{
        IntArray instructions = new IntArray();
        Scanner scan = new Scanner(new ByteArrayInputStream(input.getBytes(Vars.charset)));
        while(scan.hasNext()){
            String instrname = scan.next();

            try{
                MasmInstruction instruction = MasmInstruction.valueOf(instrname);

            }catch(IllegalArgumentException e){
                throw new MasmParseException("Instruction name not found: " + instrname);
            }
        }

        return instructions.toArray();
    }

    public static class MasmParseException extends Exception{
        public MasmParseException(String s){
            super(s);
        }

        public MasmParseException(String s, Throwable throwable){
            super(s, throwable);
        }
    }
}
