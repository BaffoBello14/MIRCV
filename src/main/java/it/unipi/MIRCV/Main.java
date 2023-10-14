package it.unipi.MIRCV;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception{

        DataOutputStream dataOutputStream=new DataOutputStream(new FileOutputStream("data.dat"));
        dataOutputStream.write("ciao".getBytes("UTF-8"));
        //dataOutputStream.writeChar('\t');
        dataOutputStream.writeInt(10);
        dataOutputStream.close();
        RandomAccessFile randomAccessFile=new RandomAccessFile("data.dat","r");
        randomAccessFile.seek(0);
        DataInputStream dataInputStream=new DataInputStream(new FileInputStream(randomAccessFile.getFD()));
        byte [] ff=new byte[4];
        dataInputStream.read(ff);


        System.out.print(new String(ff));
        //System.out.print(dataInputStream.readChar());
        System.out.println(dataInputStream.readInt());


    }
}
