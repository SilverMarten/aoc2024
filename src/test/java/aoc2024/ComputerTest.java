package aoc2024;

import static aoc._2024.Computer.REGISTER_A;
import static aoc._2024.Computer.REGISTER_B;
import static aoc._2024.Computer.REGISTER_C;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;

import aoc._2024.Computer;

@Timeout(value = 1, unit = TimeUnit.SECONDS, threadMode = ThreadMode.SEPARATE_THREAD)
class ComputerTest {

    /**
     * If register C contains 9, the program 2,6 would set register B to 1.
     */
    @Test
    void testBStore() {
        Computer state = new Computer();
        state.registers().put(REGISTER_C, 9);

        state.run(List.of(2, 6));

        assertEquals(1, state.registers().get(REGISTER_B));
    }



    /**
     * If register A contains 10, the program 5,0,5,1,5,4 would output 0,1,2.
     */
    @Test
    void testOutput() {
        Computer state = new Computer();
        state.registers().put(REGISTER_A, 10);

        state.run(List.of(5, 0, 5, 1, 5, 4));

        assertEquals(List.of(0, 1, 2), state.output());
    }



    /**
     * If register A contains 2024, the program 0,1,5,4,3,0 would output
     * 4,2,5,6,7,7,7,7,3,1,0 and leave 0 in register A.
     */
    @Test
    @Timeout(value = 1,unit = TimeUnit.HOURS)
    void testADivide() {
        Computer state = new Computer();
        state.registers().put(REGISTER_A, 2024);

        state.run(List.of(0, 1, 5, 4, 3, 0));

        assertEquals(List.of(4, 2, 5, 6, 7, 7, 7, 7, 3, 1, 0), state.output());
        assertEquals(0, state.registers().get(REGISTER_A));
    }



    /**
     * If register B contains 29, the program 1,7 would set register B to 26.
     */
    @Test
    void testBXorLiteral() {
        Computer state = new Computer();
        state.registers().put(REGISTER_B, 29);

        state.run(List.of(1, 7));

        assertEquals(26, state.registers().get(REGISTER_B));
    }



    /**
     * If register B contains 2024 and register C contains 43690, the program
     * 4,0 would set register B to 44354.
     */
    @Test
    void testBXorC() {
        Computer state = new Computer();
        state.registers().put(REGISTER_B, 2024);
        state.registers().put(REGISTER_C, 43_690);

        state.run(List.of(4, 0));

        assertEquals(44_354, state.registers().get(REGISTER_B));

    }
}
