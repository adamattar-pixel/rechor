module ReCHor {
    //requires javafx.controls;
    requires java.net.http;
    requires javafx.controls;
    requires java.desktop;
    requires jdk.jdi;

    exports ch.epfl.rechor;
    exports ch.epfl.rechor.timetable;
    exports ch.epfl.rechor.gui;
    exports ch.epfl.rechor.journey;
    exports ch.epfl.rechor.timetable.mapped;
}