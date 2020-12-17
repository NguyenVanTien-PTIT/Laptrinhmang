/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author tieng
 */
public class ResultGame implements Serializable{
    private int idRsGame;
    private String id1;
    private String id2;
    private String timeWin;
    private String timeLose;
    private ArrayList<ResultGame> list;
    public ResultGame() {
    }

    public ResultGame(int idRsGame, String id1, String id2, String timeWin, String timeLose) {
        this.idRsGame = idRsGame;
        this.id1 = id1;
        this.id2 = id2;
        this.timeWin = timeWin;
        this.timeLose = timeLose;
    }

    public int getIdRsGame() {
        return idRsGame;
    }

    public void setIdRsGame(int idRsGame) {
        this.idRsGame = idRsGame;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getTimeWin() {
        return timeWin;
    }

    public void setTimeWin(String timeWin) {
        this.timeWin = timeWin;
    }

    public String getTimeLose() {
        return timeLose;
    }

    public void setTimeLose(String timeLose) {
        this.timeLose = timeLose;
    }

    public ArrayList<ResultGame> getList() {
        return list;
    }

    public void setList(ArrayList<ResultGame> list) {
        this.list = list;
    }
    
}
