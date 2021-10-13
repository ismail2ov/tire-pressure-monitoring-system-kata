package tddmicroexercises.tirepressuremonitoringsystem;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlarmShould {

    @Test
    void be_on_when_pressure_value_is_too_low() {
        SensorMock sensor = new SensorMock();
        Alarm alarm = new Alarm(sensor);

        sensor.stubPopNextPressurePsiValue(10d);

        alarm.check();

        assertThat(alarm.isAlarmOn()).isTrue();
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