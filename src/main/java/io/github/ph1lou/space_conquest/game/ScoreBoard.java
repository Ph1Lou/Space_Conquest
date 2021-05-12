package io.github.ph1lou.space_conquest.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.space_conquest.enums.State;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoard {

    private final GameManager game;
    private final List<String> scoreBoardLobby;
    private final List<String> scoreBoardGame;

    public ScoreBoard(GameManager game){
        this.game=game;
        this.scoreBoardLobby = game.translateArray("space-conquest.score-board.lobby");
        this.scoreBoardGame = game.translateArray("space-conquest.score-board.lobby");
    }

    public void updateGlobalLobby(){
        List<String> scoreBoard = new ArrayList<>(scoreBoardLobby);
        scoreBoardLobby.clear();

        for(String line: scoreBoard){
            line=line.replace("&players&",game.getPlayerSize()+"");
            line=line.replace("&teamSize&",game.getTeams().size()+"");
            line=line.replace("&max&",game.getPlayerMax()+"");
            line=line.replace("&name&",game.getGameName());
            scoreBoardLobby.add(line);
        }
    }

    public void updateGlobalGameBoard(){

        List<String> scoreBoard = new ArrayList<>(scoreBoardGame);
        scoreBoardGame.clear();

        for(String line:scoreBoard){
            line=line.replace("&timer&",conversion(game.getTimer()));
            line=line.replace("&players&",game.getPlayerSize()+"");
            line=line.replace("&teamSize&",game.getTeams().size()+"");
            line=line.replace("&name&",game.getGameName());
            scoreBoardGame.add(line);
        }



    }

    public void updateTeamGameBoard(Team team){

        List<String> scoreBoard = new ArrayList<>(scoreBoardGame);
        List<String> scoreBoardResult = new ArrayList<>();

        for(String line:scoreBoard){
            line=line.replace("&team&",team.getName());
            line=line.replace("&color&",game.translate(team.getColorTeam().getName()));
            scoreBoardResult.add(line);
        }

        team.setScoreBoard(scoreBoardResult);
    }

    public void updatePlayerGameBoard(FastBoard fastBoard) {
        Player player = fastBoard.getPlayer();
        Team team = game.getTeam(player);
        if(team==null) return;
        List<String> scoreBoard = new ArrayList<>(team.getScoreBoard());
        List<String> scoreBoardResult = new ArrayList<>();
        for(String line:scoreBoard){
            line=line.replace("&kill&",game.getKills().getOrDefault(player.getUniqueId(),0)+"");
            scoreBoardResult.add(line);
        }
        fastBoard.updateLines(scoreBoardResult);
    }

    public void updateScoreBoard(){

        if(game.isState(State.LOBBY)){
            updateGlobalLobby();
        }
        else {
            updateGlobalGameBoard();
            for(Team team:game.getTeams()){
                updateTeamGameBoard(team);
            }
        }

        for(FastBoard fastBoard:game.getFastBoard().values()){
            if(game.isState(State.LOBBY)){
                fastBoard.updateLines(scoreBoardLobby);
            }
            else {
                updatePlayerGameBoard(fastBoard);
                Team team =game.getTeam(fastBoard.getPlayer());
                fastBoard.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("Crying Obsidian §b%d/%d",team==null?0:team.getResource().getOrDefault(Material.CRYING_OBSIDIAN,0),game.getObjective())));
            }
        }
    }

    public String conversion(int timer) {

        String value;
        float sign = Math.signum(timer);
        timer = Math.abs(timer);

        if (timer % 60 > 9) {
            value = timer % 60 + "s";
        } else value = "0" + timer % 60 + "s";

        if(timer/3600>0) {

            if(timer%3600/60>9) {
                value = timer/3600+"h"+timer%3600/60+"m"+value;
            } else value = timer/3600+"h0"+timer%3600/60+"m"+value;
        } else if (timer / 60 > 0) {
            value = timer / 60 + "m" + value;
        }
        if (sign < 0) value = "-" + value;

        return value;
    }

}
