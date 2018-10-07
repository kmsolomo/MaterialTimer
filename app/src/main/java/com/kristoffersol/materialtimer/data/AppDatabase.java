package com.kristoffersol.materialtimer.data;

public class AppDatabase {

    private static volatile AppDatabase instance = null;
    public PomodoroDao pomodoroDao;

    private AppDatabase(){
        pomodoroDao = new PomodoroDao();
    }

    public static AppDatabase getInstance(){
        if(instance == null){
            synchronized (AppDatabase.class){
                if(instance == null){
                    instance = new AppDatabase();
                }
            }
        }
        return instance;
    }
}
