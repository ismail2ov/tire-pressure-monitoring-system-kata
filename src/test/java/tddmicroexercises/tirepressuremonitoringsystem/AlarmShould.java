package tddmicroexercises.tirepressuremonitoringsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlarmShould {
    private static final double LOW_PRESSURE_THRESHOLD = 17;

    private SensorMock sensor;
    private Alarm alarm;

    @BeforeEach
    void setUp() {
        this.sensor = new SensorMock();
        this.alarm = new Alarm(sensor);
    }

    @Test
    void be_on_when_pressure_value_is_too_low() {
        sensor.stubPopNextPressurePsiValue(10d);

        alarm.check();

        assertThat(alarm.isAlarmOn()).isTrue();
    }

    @Test
    void be_on_when_pressure_value_is_too_high() {
        sensor.stubPopNextPressurePsiValue(25d);

        alarm.check();

        assertThat(alarm.isAlarmOn()).isTrue();
    }

    @Test
    void be_off_when_pressure_equals_low_threshold() {
        sensor.stubPopNextPressurePsiValue(LOW_PRESSURE_THRESHOLD);

        alarm.check();

        assertThat(alarm.isAlarmOn()).isFalse();
    }

    private class SensorMock implements Sensor {

        private final List<Double> stubs = new ArrayList<>();
        private int numInteractions = 0;

        @Override
        public double popNextPressurePsiValue() {
            return stubs.get(numInteractions++);
        }

        public void stubPopNextPressurePsiValue(double value) {
            this.stubs.add(value);
        }
    }
}