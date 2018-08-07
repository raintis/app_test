package com.my.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import com.google.common.base.Stopwatch;

public class DeflaterOutputStreamIO {
    private static String inPath = "c:\\uncompress.txt";
    private static String outPath = "c:\\compress.txt";

    public static void main(String[] args) throws Exception {
    	Stopwatch watch = Stopwatch.createStarted();
        // 压缩文件
         deflater(inPath,outPath);
         System.out.println("deflater time -->"+watch.toString());
         watch.reset();
         watch.start();
        // 解压文件
        Inflater(outPath, "c:\\uncompress2.txt");
        System.out.println("Inflater time -->"+watch.toString());
    }

    /**
     * 
     * @param outPath2
     *            压缩后的文件
     * @param str
     *            解压后的文件
     * @throws Exception
     */
    private static void Inflater(String outPath2, String str) throws Exception {
        FileInputStream fis = new FileInputStream(new File(outPath));
        FileOutputStream fos = new FileOutputStream(new File(str));
        InflaterOutputStream ios = new InflaterOutputStream(fos, new Inflater(true));

        byte[] b = new byte[1024];
        int len = 0;
        while ((len = fis.read(b)) != -1) {
            ios.write(b, 0, len);
        }
        fis.close();
        ios.close();
    }

    /**
     * 
     * @param inPath
     *            原文件
     * @param outPath
     *            压缩后的文件
     * @throws Exception
     */
    public static void deflater(String inPath, String outPath) throws Exception {
        FileInputStream fis = new FileInputStream(new File(inPath));
        FileOutputStream fos = new FileOutputStream(new File(outPath));
        DeflaterOutputStream dos = new DeflaterOutputStream(fos,
                new Deflater(1,true));

        byte[] b = new byte[1024];
        int len = 0;
        while ((len = fis.read(b)) != -1) {
            dos.write(b, 0, len);
        }
        fis.close();
        dos.close();
    }
}
