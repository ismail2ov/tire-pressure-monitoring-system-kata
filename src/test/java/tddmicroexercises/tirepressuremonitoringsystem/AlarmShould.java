package tddmicroexercises.tirepressuremonitoringsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.verification.TooFewActualInvocations;
import org.mockito.exceptions.verification.TooManyActualInvocations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmShould {
    private static final double LOW_PRESSURE_THRESHOLD = 17;
    private static final double HIGH_PRESSURE_THRESHOLD = 21;

    @Mock
    private Sensor sensor;
    private Alarm alarm;

    @BeforeEach
    void setUp() {
        this.alarm = spy(new Alarm(sensor));
    }

    @Test
    void be_on_when_pressure_value_is_too_low() {
        when(sensor.popNextPressurePsiValue()).thenReturn(10d);

        alarm.check();

        assertThat(alarm.isAlarmOn()).isTrue();
    }

    @Test
    void be_on_when_pressure_value_is_too_high() {
        when(sensor.popNextPressurePsiValue()).thenReturn(25d);

        alarm.check();

        assertThat(alarm.isAlarmOn()).isTrue();
    }

    @Test
    void be_off_when_pressure_equals_low_threshold() {
        when(sensor.popNextPressurePsiValue()).thenReturn(LOW_PRESSURE_THRESHOLD);

        alarm.check();

        assertThat(alarm.isAlarmOn()).isFalse();
    }

    @Test
    void be_off_when_pressure_equals_high_threshold() {
        when(sensor.popNextPressurePsiValue()).thenReturn(HIGH_PRESSURE_THRESHOLD);

        alarm.check();

        assertThat(alarm.isAlarmOn()).isFalse();
    }

    @Test
    void be_on_when_the_pressure_value_of_the_last_measurement_is_too_low() {
        when(sensor.popNextPressurePsiValue()).thenReturn(LOW_PRESSURE_THRESHOLD);
        when(sensor.popNextPressurePsiValue()).thenReturn(HIGH_PRESSURE_THRESHOLD);
        when(sensor.popNextPressurePsiValue()).thenReturn(10d);

        alarm.check();
        alarm.check();
        alarm.check();

        assertThat(alarm.isAlarmOn()).isTrue();
    }

    @Test
    void be_on_when_the_pressure_value_of_the_last_measurement_is_too_high() {
        when(sensor.popNextPressurePsiValue()).thenReturn(LOW_PRESSURE_THRESHOLD);
        when(sensor.popNextPressurePsiValue()).thenReturn(HIGH_PRESSURE_THRESHOLD);
        when(sensor.popNextPressurePsiValue()).thenReturn(25d);

        alarm.check();
        alarm.check();
        alarm.check();

        assertThat(alarm.isAlarmOn()).isTrue();
    }

    @Test
    void be_off_when_the_pressure_value_of_the_last_measurement_is_within_the_range() {
        when(sensor.popNextPressurePsiValue()).thenReturn(25d);
        when(sensor.popNextPressurePsiValue()).thenReturn(10d);
        when(sensor.popNextPressurePsiValue()).thenReturn(19d);

        alarm.check();
        alarm.check();
        alarm.check();

        assertThat(alarm.isAlarmOn()).isFalse();
    }

    @Test
    void call_psi_pressure_value() {
        when(sensor.popNextPressurePsiValue()).thenReturn(19d);

        alarm.check();

        verify(alarm).check();
        verify(alarm).getPsiPressureValue();
        verifyNoMoreInteractions(alarm);
    }

    @Test
    void throw_an_exception_when_there_are_unexpected_interactions() {
        when(sensor.popNextPressurePsiValue()).thenReturn(19d);

        alarm.check();
        alarm.check();

        assertThatThrownBy(() -> {
            verify(alarm).check();
            verify(alarm).getPsiPressureValue();
            verifyNoMoreInteractions(alarm);
        })
                .isInstanceOf(TooManyActualInvocations.class);

    }

    @Test
    void throw_an_exception_when_there_are_unverified_interactions() {
        when(sensor.popNextPressurePsiValue()).thenReturn(19d);

        alarm.check();

        assertThatThrownBy(() -> {
            verify(alarm, times(2)).check();
            verify(alarm).getPsiPressureValue();
            verifyNoMoreInteractions(alarm);
        })
                .isInstanceOf(TooFewActualInvocations.class);
    }

}