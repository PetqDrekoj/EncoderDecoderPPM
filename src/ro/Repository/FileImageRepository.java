package ro.Repository;

import ro.Domain.Block;
import ro.Domain.Pixel;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileImageRepository {

    int resolutionWidth, resolutionHeight, maxValueOfByteComponent;
    private List<Pixel> pixelList;
    private List<Block> blockList;

    public FileImageRepository() {
        pixelList = new ArrayList<>();
        blockList = new ArrayList<>();
    }

    public void getEncodedDataFromFile(String fileName) {
        try {
            File file = new File(fileName);
            Scanner fileDescriptor = new Scanner(file);

            String format = "no format";

            int line_number = 1;
            while (fileDescriptor.hasNextLine()) {
                String readline = fileDescriptor.nextLine();
                if (!readline.contains("#")) {    // not getting the comments.
                    if (line_number == 1) format = readline;  // first line should be the format
                    else if (line_number == 2) {
                        resolutionWidth = Integer.parseInt(readline.split(" ")[0]);
                        resolutionHeight = Integer.parseInt(readline.split(" ")[1]);
                    } else if (line_number == 3) {
                        maxValueOfByteComponent = Integer.parseInt(readline);
                    } else {
                        if (format.equals("P3")) {
                            int red = Integer.parseInt(readline), green = 0, blue = 0;
                            if (fileDescriptor.hasNextLine()) green = Integer.parseInt(fileDescriptor.nextLine());
                            if (fileDescriptor.hasNextLine()) blue = Integer.parseInt(fileDescriptor.nextLine());
                            pixelList.add(new Pixel(red, green, blue));
                        } else if (format.equals("P6")) {
                            System.out.println("This format is not accepted");
                            break;
                        } else {
                            System.out.println("format is corrupted");
                            break;
                        }
                    }
                    line_number += 1;
                }
                System.out.println(pixelList.size() + " pixels read");
            }
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " was not found.");
        }
    }


    public void getEncodedDataFromBinFile(String fileName) {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {
            int linenumber = 1;
            String format = "";
            while (linenumber < 4) {
                char startingChar = (char) in.readByte();
                if (startingChar != '#') {
                    if (linenumber == 1) format = startingChar + readWordBinary(in);
                    else if (linenumber == 2) {
                        String resWidth = startingChar + readWordBinary(in), resHeight = readWordBinary(in);
                        resolutionWidth = Integer.parseInt(resWidth);
                        resolutionHeight = Integer.parseInt(resHeight);
                    } else if (linenumber == 3) {
                        String maxValue = startingChar + readWordBinary(in);
                        this.maxValueOfByteComponent = Integer.parseInt(maxValue);
                    }
                    linenumber += 1;
                } else skipLine(in);
            }

            if (format.equals("P3")) {
                int red, green, blue;
                while (linenumber < resolutionWidth * resolutionHeight + 4) {
                    red = Integer.parseInt(readWordBinary(in));
                    green = Integer.parseInt(readWordBinary(in));
                    blue = Integer.parseInt(readWordBinary(in));
                    pixelList.add(new Pixel(red, green, blue));
                    System.out.println(pixelList.size() + " pixels read");
                    linenumber += 1;
                }
            } else {
                int red, green, blue;
                char startingChar = ' ';
                while (linenumber < resolutionWidth * resolutionHeight + 4) {
                    red = in.read();
                    green = in.read();
                    blue = in.read();
                    pixelList.add(new Pixel(red, green, blue));
                    System.out.println(pixelList.size() + " pixels read");
                    linenumber += 1;
                }
            }
        } catch (IOException s) {
            s.printStackTrace();
        }
    }

    private String readWordBinary(DataInputStream in) throws IOException {
        String s = "";
        char c = (char) in.readByte();
        while (c != ' ' && c != '\n') {
            s += c;
            c = (char) in.readByte();
        }
        if (s.contains("#")) return readWordBinary(in);
        else return s;
    }

    private void skipLine(DataInputStream in) throws IOException {
        char c = (char) in.readByte();
        while (c != '\n') {
            c = (char) in.readByte();
        }
    }


    public void getDecodedDataFromFile(String fileName) {
        try {
            File file = new File(fileName);
            Scanner fileDescriptor = new Scanner(file);
            String firstLine = fileDescriptor.nextLine();
            this.resolutionWidth = Integer.parseInt(firstLine.split(" ")[0]);
            this.resolutionHeight = Integer.parseInt(firstLine.split(" ")[1]);
            while (fileDescriptor.hasNextLine()) {
                String readline = fileDescriptor.nextLine();
                String[] strings = readline.split(" ");
                this.blockList.add(new Block(Arrays.stream(strings).skip(5).map(Double::parseDouble).collect(Collectors.toList()), strings[0].charAt(0), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4])));
            }
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " was not found.");
        }
    }


    public List<Pixel> getPixelList() {
        return pixelList;
    }

    public void setPixelList(List<Pixel> pixelList) {
        this.pixelList = pixelList;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public void setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public void setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    public int getMaxValueOfByteComponent() {
        return maxValueOfByteComponent;
    }

    public void setMaxValueOfByteComponent(int maxValueOfByteComponent) {
        this.maxValueOfByteComponent = maxValueOfByteComponent;
    }

    public List<Block> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<Block> blockList) {
        this.blockList = blockList;
    }
}
