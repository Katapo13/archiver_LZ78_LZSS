package basic;

import java.io.*;
import java.util.List;

public abstract class Decompressor {
    public abstract List<byte[]> decompress(File inputFile) throws IOException;
}
