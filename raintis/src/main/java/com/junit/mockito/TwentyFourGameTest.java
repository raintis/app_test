package com.junit.mockito;

import com.wisecoders.dbschema.dbf.TwentyFourGame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwentyFourGameTest {

    @Test
    void testCanGetTwentyFour_SingleNumber_True() {
        List<Double> numbers = Collections.singletonList(24.0);
        assertTrue(TwentyFourGame.canGetTwentyFour(numbers));
    }

    @Test
    void testCanGetTwentyFour_SingleNumber_True_Epsilon() {
        List<Double> numbers = Collections.singletonList(23.999999);
        assertTrue(TwentyFourGame.canGetTwentyFour(numbers));
    }

    @Test
    void testCanGetTwentyFour_SingleNumber_False() {
        List<Double> numbers = Collections.singletonList(25.0);
        assertFalse(TwentyFourGame.canGetTwentyFour(numbers));
    }

    @Test
    void testCanGetTwentyFour_RecursiveCase_True() {
        List<Double> numbers = Arrays.asList(1.0, 3.0, 4.0, 6.0);
        assertTrue(TwentyFourGame.canGetTwentyFour(numbers));
    }

    @Test
    void testCanGetTwentyFour_RecursiveCase_False() {
        List<Double> numbers = Arrays.asList(1.0, 1.0, 1.0, 1.0);
        assertFalse(TwentyFourGame.canGetTwentyFour(numbers));
    }

    @Test
    void testCanGetTwentyFour_BoundaryCase_AllZeros() {
        List<Double> numbers = Arrays.asList(0.0, 0.0, 0.0, 0.0);
        assertFalse(TwentyFourGame.canGetTwentyFour(numbers));
    }

    @Test
    void testCanGetTwentyFour_BoundaryCase_OneTwentyFour() {
        List<Double> numbers = Arrays.asList(24.0, 0.0, 0.0, 0.0);
        assertTrue(TwentyFourGame.canGetTwentyFour(numbers));
    }

    @Test
    void testCanGetTwentyFour_MockStaticMethod() {
        try (MockedStatic<TwentyFourGame> mocked = Mockito.mockStatic(TwentyFourGame.class)) {
            // 模拟静态方法的行为
            mocked.when(() -> TwentyFourGame.canGetTwentyFour(anyList())).thenReturn(true);

            List<Double> numbers = Arrays.asList(1.0, 2.0, 3.0, 4.0);
            assertTrue(TwentyFourGame.canGetTwentyFour(numbers));

            // 验证静态方法是否被调用
            mocked.verify(() -> TwentyFourGame.canGetTwentyFour(numbers));
        }
    }
}
