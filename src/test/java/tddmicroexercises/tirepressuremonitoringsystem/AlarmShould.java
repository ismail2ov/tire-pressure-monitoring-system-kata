package tddmicroexercises.tirepressuremonitoringsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlarmShould {
    private static final double LOW_PRESSURE_THRESHOLD = 17;
    private static final double HIGH_PRESSURE_THRESHOLD = 21;

    private SensorMock sensor;
    private AlarmSpy alarm;

    @BeforeEach
    void setUp() {
        this.sensor = new SensorMock();
        this.alarm = new AlarmSpy(sensor);
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

    @Test
    void be_off_when_pressure_equals_high_threshold() {
        sensor.stubPopNextPressurePsiValue(HIGH_PRESSURE_THRESHOLD);

        alarm.check();

        assertThat(alarm.isAlarmOn()).isFalse();
    }

    @Test
    void be_on_when_the_pressure_value_of_the_last_measurement_is_too_low() {
        sensor.stubPopNextPressurePsiValue(LOW_PRESSURE_THRESHOLD);
        sensor.stubPopNextPressurePsiValue(HIGH_PRESSURE_THRESHOLD);
        sensor.stubPopNextPressurePsiValue(10d);

        alarm.check();
        alarm.check();
        alarm.check();

        assertThat(alarm.isAlarmOn()).isTrue();
    }

    @Test
    void be_on_when_the_pressure_value_of_the_last_measurement_is_too_high() {
        sensor.stubPopNextPressurePsiValue(LOW_PRESSURE_THRESHOLD);
        sensor.stubPopNextPressurePsiValue(HIGH_PRESSURE_THRESHOLD);
        sensor.stubPopNextPressurePsiValue(25d);

        alarm.check();
        alarm.check();
        alarm.check();

        assertThat(alarm.isAlarmOn()).isTrue();
    }

    @Test
    void be_off_when_the_pressure_value_of_the_last_measurement_is_within_the_range() {
        sensor.stubPopNextPressurePsiValue(25d);
        sensor.stubPopNextPressurePsiValue(10d);
        sensor.stubPopNextPressurePsiValue(19d);

        alarm.check();
        alarm.check();
        alarm.check();

        assertThat(alarm.isAlarmOn()).isFalse();
    }

    @Test
    void call_psi_pressure_value() throws Exception {
        sensor.stubPopNextPressurePsiValue(19d);

        alarm.check();

        alarm.verify("check");
        alarm.verify("getPsiPressureValue");
        alarm.verifyNoMoreInteractions();
    }

    @Test
    void throw_an_exception_when_there_are_unexpected_interactions() throws Exception {
        sensor.stubPopNextPressurePsiValue(19d);

        alarm.check();
        alarm.check();

        alarm.verify("check");
        alarm.verify("getPsiPressureValue");

        assertThatThrownBy(() -> alarm.verifyNoMoreInteractions())
                .isInstanceOf(Exception.class);

    }

    @Test
    void throw_an_exception_when_there_are_unverified_interactions() throws Exception {
        sensor.stubPopNextPressurePsiValue(19d);

        alarm.check();

        assertThatThrownBy(() -> {
            alarm.verify("check");
            alarm.verify("check");
            alarm.verify("getPsiPressureValue");
            alarm.verifyNoMoreInteractions();
        })
                .isInstanceOf(Exception.class);
    }

    private class SensorMock implements Sensor {

        private final List<Double> stubs = new ArrayList<>();
        private int numInteractions = 0;

        @Override
        public double popNextPressurePsiValue() {
            if (numInteractions >= stubs.size()) {
                return -1;
            }
            return stubs.get(numInteractions++);
        }

        public void stubPopNextPressurePsiValue(double value) {
            this.stubs.add(value);
        }
    }

    private class AlarmSpy extends Alarm {
        private List<String> interaction = new ArrayList<>();

        public AlarmSpy(SensorMock sensor) {
            super(sensor);
        }

        public void check() {
            this.interaction.add("check");
            super.check();
        }

        public boolean isAlarmOn() {
            this.interaction.add("isAlarmOn");
            return super.isAlarmOn();
        }

        protected double getPsiPressureValue() {
            this.interaction.add("getPsiPressureValue");
            return super.getPsiPressureValue();
        }

        public void verify(String methodName) throws Exception {
            if (interaction.isEmpty()) {
                throw new Exception();
            }

            for (String method : interaction) {
                if (method.equals(methodName)) {
                    interaction.remove(method);
                    return;
                }
            }

            throw new Exception();
        }

        public void verifyNoMoreInteractions() throws Exception {
            if (interaction.size() > 0) {
                throw new Exception();
            }
        }
    }
}