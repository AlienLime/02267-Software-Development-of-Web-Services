package dtu.group17.steps;

import dtu.group17.ErrorMessageHolder;
import dtu.group17.Holder;

public class ReportSteps {
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;

    public ReportSteps(Holder holder, ErrorMessageHolder errorMessageHolder) {
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
    }
}
