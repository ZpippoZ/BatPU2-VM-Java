package org.example;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        int[] lines = new int[27];

        lines[0]  = 0b1000000111111111;
        lines[1]  = 0b1000001011111111;
        lines[2]  = 0b1000001111111111;
        lines[3]  = 0b0000000000000000;
        lines[4]  = 0b0000000000000000;
        lines[5]  = 0b0000000000000000;
        lines[6]  = 0b0010000000000000;
        lines[7]  = 0b0011000000000000;
        lines[8]  = 0b0100000000000000;
        lines[9]  = 0b0101000000000000;
        lines[10] = 0b0110000000000000;
        lines[11] = 0b0111000000000000;
        lines[12] = 0b1000000000000000;
        lines[13] = 0b1001000111111111;
        lines[14] = 0b1011000000010011;
        lines[15] = 0b1100000000011010;
        lines[16] = 0b1110000000000000;
        lines[17] = 0b1111000000000000;
        lines[18] = 0b1010000000000100;
        lines[19] = 0b1001001011111111;
        lines[20] = 0b1011000000010110;
        lines[21] = 0b1010000000000100;
        lines[22] = 0b1001001111111111;
        lines[23] = 0b1011000000011001;
        lines[24] = 0b1010000000000011;
        lines[25] = 0b0001000000000000;
        lines[26] = 0b1101000000000000;

        int pc = 0;
        boolean[] flags = {false, false};
        int[] regs = new int[16];
        int[] ram = new int[256];
        ArrayList<Integer> stack = new ArrayList<>(32);

        int cycles = 0;
        int result;

        long start_time = System.nanoTime();

        boolean running = true;

        while(running) {
            regs[0] = 0;

            int instruction = lines[pc];

            int opcode = instruction >> 12;
            int regA = instruction >> 8 & 15;
            int regB = instruction >> 4 & 15;
            int regDest = instruction & 15;
            int immediate = instruction & 255;
            int condition = instruction >> 10 & 3;
            int address = instruction & 1023;
            int ram_offset = instruction & 15;
            ram_offset = (ram_offset & 8) != 0 ? ram_offset - 16 : ram_offset;

            pc += 1;
            cycles += 1;

            switch (opcode) {
                case 1:
                    running = false;
                    System.out.println("\nHalted\n");
                    break;
                case 2:
                    result = regs[regA] + regs[regB];
                    regs[regDest] = result & 255;
                    flags[0] = result > 255;
                    flags[1] = regs[regDest] == 0;
                    break;
                case 3:
                    result = regs[regA] - regs[regB];
                    flags[0] = regs[regA] >= regs[regB];
                    regs[regDest] = result & 255;
                    flags[1] = regs[regDest] == 0;
                    break;
                case 4:
                    regs[regDest] = 0b11111111 ^ (regs[regA] | regs[regB]);
                    flags[1] = regs[regDest] == 0;
                    break;
                case 5:
                    regs[regDest] = regs[regA] & regs[regB];
                    flags[1] = regs[regDest] == 0;
                    break;
                case 6:
                    regs[regDest] = regs[regA] ^ regs[regB];
                    flags[1] = regs[regDest] == 0;
                    break;
                case 7:
                    regs[regDest] = regs[regA] >> 1;
                    break;
                case 8:
                    regs[regA] = immediate;
                    break;
                case 9:
                    result = regs[regA] + immediate;
                    regs[regA] = result & 255;
                    // System.out.print(regA == 3 ? String.valueOf(regs[regA]) + "\n" : "");
                    flags[0] = result > 255;
                    flags[1] = regs[regA] == 0;
                    break;
                case 10:
                    pc = address;
                    break;
                case 11:
                    switch (condition) {
                        case 0 -> pc = flags[1] ? address : pc;
                        case 1 -> pc = !flags[1] ? address : pc;
                        case 2 -> pc = flags[0] ? address : pc;
                        case 3 -> pc = !flags[0] ? address : pc;
                    }
                    break;
                case 12:
                    stack.add(pc);
                    pc = address;
                    break;
                case 13:
                    pc = stack.remove(stack.size() - 1);
                    break;
                case 14:
                    regs[regB] = ram[regA + ram_offset];
                    break;
                case 15:
                    ram[regA + ram_offset] = regs[regB];
                    break;
            }

        }

        long end_time = System.nanoTime();
        double time = (double) (end_time - start_time) / 1_000_000_000;

        System.out.println(cycles + " cycles executed (" + cycles / 1_000_000 + "m)");
        System.out.println("The program ran for " + String.format("%.3f", time) + " seconds");
        System.out.println("Average instruction time: " + String.format("%.10f", time / cycles) + " seconds");
        System.out.println("Average instructions per second: " + String.format("%.3f", cycles / time) + " (" + (int) (cycles / time / 1_000_000) + "m ips)");

    }

}