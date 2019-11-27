package io.anuke.mindustry.masm;

import io.anuke.arc.collection.*;
import io.anuke.mindustry.*;

import java.io.*;
import java.util.*;

public class MasmParser{
    /*
    Syntax:

    1. variable operations. 'op' indicates a standard operator (+, -, ==, *, %, /, ^, <<, >>, ~)
    x = y op z # y and z are variables or literals; each literal is a signed short
    x op= y    # compiles to x = x op y
    x = y      # assigns a variable to a direct literal
    2. labels
    <name>: <optional newline>
    3. jumps. 'cond' can be one of: (<, >, <=, >=, ==, !=)
    jump <label> x cond y
    4. sensor calls
    x = <method> <name> <parameters..>
    5. memory
    x = y:z  #read from memory
    x:y = z  #write to memory
     */

    public int[] parse(String input) throws MasmParseException{
        IntArray instructions = new IntArray();
        Scanner scan = new Scanner(new ByteArrayInputStream(input.getBytes(Vars.charset)));
        StringReader reader = new StringReader(input);

        while(scan.hasNext()){
            String instrname = scan.next();

            try{
                //MasmInstruction instruction = MasmInstruction.valueOf(instrname);

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
