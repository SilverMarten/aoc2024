package aoc._2024;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A representation of a computer from Day 17.
 */
public record Computer(Map<String, Long> registers, List<Integer> output) {

    public static final String REGISTER_A = "Register A";

    public static final String REGISTER_B = "Register B";

    public static final String REGISTER_C = "Register C";

    public static final String INSTRUCTION_POINTER = "Instruction pointer";



    public Computer() {
        this(new HashMap<>(), new ArrayList<>());
        this.registers.put(INSTRUCTION_POINTER, 0L);
    }



    /**
     * Execute the entire program.
     * 
     * @param program The program instructions and inputs.
     */
    public void run(List<Integer> program) {
        while (this.step(program))
            ;
    }



    /**
     * Execute the next instruction in the program based on the instruction
     * pointer.
     * 
     * @param program The program instructions and inputs.
     * @return {@code true} if the program can continue. {@code false} if the
     *         instruction pointer is past the end of the program.
     */
    public boolean step(List<Integer> program) {
        int ip = getInstructionPointer();
        if (ip < program.size()) {
            var instruction = Instruction.fromOpcode(program.get(ip));
            int input = program.get(ip + 1);

            instruction.execute(this, input);
        }
        return this.getInstructionPointer() < program.size();
    }



    /**
     * Reset all registers to 0, and clear the output.
     */
    public void reset() {
        this.registers.replaceAll((k, v) -> 0L);
        this.output.clear();

    }



    private int getInstructionPointer() {
        return this.registers.get(INSTRUCTION_POINTER).intValue();
    }



    private void incrementInstructionPointer() {
        this.registers.compute(Computer.INSTRUCTION_POINTER, (k, v) -> v + 2);
    }



    @Override
    public final String toString() {
        return registers.entrySet()
                        .stream()
                        .sorted(Comparator.comparing(Entry::getKey))
                        .map(e -> e.getKey() + ": " + e.getValue())
                        .collect(Collectors.joining("\n")) +
               "\nOutput: " +
               output.stream().map(Object::toString).collect(Collectors.joining(","));
    }



    public enum Instruction {

        ADV(0, Instruction::aDivide),
        BXL(1, Instruction::bXorLiteral),
        BST(2, Instruction::bStore),
        JNZ(3, Instruction::jumpIfNotZero),
        BXC(4, Instruction::bXorC),
        OUT(5, Instruction::output),
        BDV(6, Instruction::bDivide),
        CDV(7, Instruction::cDivide);



        private int opcode;

        private BiConsumer<Computer, Integer> operation;

        private static final Map<Integer, Instruction> map = Stream.of(Instruction.values())
                                                                   .collect(Collectors.toUnmodifiableMap(i -> i.opcode,
                                                                                                         Function.identity()));



        Instruction(int opcode, BiConsumer<Computer, Integer> operation) {
            this.opcode = opcode;
            this.operation = operation;
        }



        public static Instruction fromOpcode(int opcode) {
            return map.get(opcode);
        }



        public int getOpcode() {
            return this.opcode;
        }



        /**
         * Execute the current command, updating the state of the computer as a
         * result.
         * 
         * @param state The current state of the computer.
         * @param input The input to the command.
         */
        public void execute(Computer state, int input) {
            this.operation.accept(state, input);
        }



        /**
         * The adv instruction (opcode 0) performs division. The numerator is
         * the value in the A register. The denominator is found by raising 2 to
         * the power of the instruction's combo operand. (So, an operand of 2
         * would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.)
         * The result of the division operation is truncated to an integer and
         * then written to the A register.
         * 
         * @param state The current state of the computer.
         * @param input The input to the command.
         */
        private static void aDivide(Computer state, int input) {
            long numerator = state.registers().get(Computer.REGISTER_A);
            double denominator = Math.pow(2, resolveComboOperand(state.registers(), input));
            long result = (long) (numerator / denominator);
            state.registers().put(Computer.REGISTER_A, result);
            state.incrementInstructionPointer();
        }



        /**
         * The bxl instruction (opcode 1) calculates the bitwise XOR of register
         * B and the instruction's literal operand, then stores the result in
         * register B.
         * 
         * @param state The current state of the computer.
         * @param input The input to the command.
         */
        private static void bXorLiteral(Computer state, int input) {
            long result = state.registers().get(Computer.REGISTER_B) ^ input;
            state.registers().put(Computer.REGISTER_B, result);
            state.incrementInstructionPointer();
        }



        /**
         * The bst instruction (opcode 2) calculates the value of its combo
         * operand modulo 8 (thereby keeping only its lowest 3 bits), then
         * writes that value to the B register.
         * 
         * @param state The current state of the computer.
         * @param input The input to the command.
         */
        private static void bStore(Computer state, int input) {
            long result = resolveComboOperand(state.registers(), input) % 8;
            state.registers().put(Computer.REGISTER_B, result);
            state.incrementInstructionPointer();
        }



        /**
         * The jnz instruction (opcode 3) does nothing if the A register is 0.
         * However, if the A register is not zero, it jumps by setting the
         * instruction pointer to the value of its literal operand; if this
         * instruction jumps, the instruction pointer is not increased by 2
         * after this instruction.
         * 
         * @param state The current state of the computer.
         * @param input The input to the command.
         */
        private static void jumpIfNotZero(Computer state, int input) {
            if (state.registers().get(Computer.REGISTER_A) != 0)
                state.registers().put(Computer.INSTRUCTION_POINTER, (long) input);
            else
                state.incrementInstructionPointer();
        }



        /**
         * The bxc instruction (opcode 4) calculates the bitwise XOR of register
         * B and register C, then stores the result in register B. (For legacy
         * reasons, this instruction reads an operand but ignores it.)
         * 
         * @param state The current state of the computer.
         * @param input The input to the command.
         */
        private static void bXorC(Computer state, int input) {
            long b = state.registers().get(Computer.REGISTER_B);
            long c = state.registers().get(Computer.REGISTER_C);
            long result = b ^ c;
            state.registers().put(Computer.REGISTER_B, result);
            state.incrementInstructionPointer();
        }



        /**
         * The out instruction (opcode 5) calculates the value of its combo
         * operand modulo 8, then outputs that value. (If a program outputs
         * multiple values, they are separated by commas.)
         * 
         * @param state The current state of the computer.
         * @param input The input to the command.
         */
        private static void output(Computer state, int input) {
            int value = (int) (resolveComboOperand(state.registers(), input) % 8);
            state.output().add(value);
            state.incrementInstructionPointer();
        }



        /**
         * The bdv instruction (opcode 6) works exactly like the adv instruction
         * except that the result is stored in the B register. (The numerator is
         * still read from the A register.)
         * 
         * @param state The current state of the computer.
         * @param input The input to the command.
         */
        private static void bDivide(Computer state, int input) {
            long numerator = state.registers().get(Computer.REGISTER_A);
            double denominator = Math.pow(2, resolveComboOperand(state.registers(), input));
            long result = (long) (numerator / denominator);
            state.registers().put(Computer.REGISTER_B, result);
            state.incrementInstructionPointer();
        }



        /**
         * The cdv instruction (opcode 7) works exactly like the adv instruction
         * except that the result is stored in the C register. (The numerator is
         * still read from the A register.)
         * 
         * @param state The current state of the computer.
         * @param input The input to the command.
         */
        private static void cDivide(Computer state, int input) {
            long numerator = state.registers().get(Computer.REGISTER_A);
            double denominator = Math.pow(2, resolveComboOperand(state.registers(), input));
            long result = (long) (numerator / denominator);
            state.registers().put(Computer.REGISTER_C, result);
            state.incrementInstructionPointer();
        }



        /**
         * Get the correct value for a combo operand. The value of a combo
         * operand can be found as follows:
         * 
         * <ul>
         * <li>Combo operands 0 through 3 represent literal values 0 through
         * 3.</li>
         * <li>Combo operand 4 represents the value of register A.</li>
         * <li>Combo operand 5 represents the value of register B.</li>
         * <li>Combo operand 6 represents the value of register C.</li>
         * <li>Combo operand 7 is reserved and will not appear in valid
         * programs.</li>
         * </ul>
         * 
         * @param registers The current state of the computer.
         * @param input The input to be interpreted as a combo operand.
         * @return The correct value based on the current state of the
         *         computer's registers and the input value.
         */
        private static long resolveComboOperand(Map<String, Long> registers, int input) {
            return switch (input) {
                case 0, 1, 2, 3 -> input;
                case 4 -> registers.get(Computer.REGISTER_A);
                case 5 -> registers.get(Computer.REGISTER_B);
                case 6 -> registers.get(Computer.REGISTER_C);
                case 7 -> throw new IllegalArgumentException("Invalid program");
                default -> throw new IllegalArgumentException("Unexpected value: " + input);
            };
        }
    }
}