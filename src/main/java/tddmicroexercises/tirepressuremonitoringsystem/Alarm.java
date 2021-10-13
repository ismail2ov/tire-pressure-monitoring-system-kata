package tddmicroexercises.tirepressuremonitoringsystem;

public class Alarm {
    private final double lowPressureThreshold = 17;
    private final double highPressureThreshold = 21;

    private Sensor sensor;

    private boolean alarmOn = false;

    public Alarm(Sensor sensor) {
        this.sensor = sensor;
    }

    public void check() {
        double psiPressureValue = getPsiPressureValue();

        if (psiPressureValue < lowPressureThreshold || highPressureThreshold < psiPressureValue) {
            alarmOn = true;
        }
    }

    public boolean isAlarmOn() {
        return alarmOn;
    }

    protected double getPsiPressureValue() {
        return sensor.popNextPressurePsiValue();
    }
}
