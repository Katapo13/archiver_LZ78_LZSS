package basic;

import java.io.*;
import java.util.List;

public abstract class Compressor {
    public abstract List<byte[]> compress(File inputFile) throws IOException;
}
