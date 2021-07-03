package io.github.ph1lou.space_conquest.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.utils.BukkitUtils;
import io.github.ph1lou.space_conquest.utils.TexturedItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreBoard {

    private final GameManager game;
    private final List<String> scoreBoardLobby = new ArrayList<>();
    private final List<String> scoreBoardGame= new ArrayList<>();

    public ScoreBoard(GameManager game){
        this.game=game;
    }

    public void updateGlobalLobby(){
        List<String> scoreBoard = new ArrayList<>(game.translateArray("space-conquest.score-board.lobby"));
        scoreBoardLobby.clear();

        for(String line: scoreBoard){
            line=line.replace("&players&",String.valueOf(game.getPlayerSize()));
            line=line.replace("&teamSize&",String.valueOf(game.getTeams().size()));
            line=line.replace("&max&",String.valueOf(game.getPlayerMax()));
            line=line.replace("&name&",game.getGameName());
            scoreBoardLobby.add(line);
        }
    }

    public void updateGlobalGameBoard(){

        List<String> scoreBoard = new ArrayList<>(game.translateArray("space-conquest.score-board.game-board"));
        scoreBoardGame.clear();

        for(String line:scoreBoard){
            line=line.replace("&timer&", BukkitUtils.conversion(game.getTimer()));
            line=line.replace("&players&",String.valueOf(game.getPlayerSize()));
            line=line.replace("&teamSize&",String.valueOf(game.getTeams().size()));
            line=line.replace("&name&",String.valueOf(game.getGameName()));
            scoreBoardGame.add(line);
        }



    }

    public void updateTeamGameBoard(Team team){

        List<String> scoreBoard = new ArrayList<>(this.scoreBoardGame);
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

        List<String> scoreBoard = new ArrayList<>(game.getTeam(player).isPresent() ?
                game.getTeam(player).get().getScoreBoard() :
                this.scoreBoardGame.stream()
                        .map(s -> s.replace("&team&","Spectateur"))
                        .map(s -> s.replace("&color&","Paillettes"))
                        .collect(Collectors.toList()));
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
                game.getTeam(fastBoard.getPlayer()).ifPresent(team -> fastBoard.getPlayer()
                        .spigot()
                        .sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText(game.translate("space-conquest.action-bar.message",
                                        team.getResource().getOrDefault(TexturedItem.CRYING_OBSIDIAN_RESSOURCE,0),
                                        game.getObjective()))));

            }
        }
    }



}
