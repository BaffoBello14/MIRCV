package it.unipi.MIRCV.Utils;

public class VariableByteEncoder {
    public static byte[] encode(int value){
        int numBytes=(int)Math.ceil((Math.log(value+1)/Math.log(128)));
        byte [] encodedBytes=new byte[numBytes];
        for (int i=numBytes-1;i>=0;i--){
            byte currentByte=(byte)(value%128);
            value/=128;
            if(i>0){
                currentByte|= (byte) 128;
            }
            encodedBytes[i]=currentByte;
        }
        return encodedBytes;
    }
    public static int decode(byte[] encodedBytes){
        int decodedValue=0;
        for (byte b:encodedBytes){
            int value=b&0x7f;
            decodedValue=(decodedValue<<7)|value;
            if((b&0x80)==0){
                break;
            }
        }
        return decodedValue;
    }

}
