package it.polimi.se2018.client.view.cli;

import it.polimi.se2018.model.Window;
import it.polimi.se2018.model.toolcards.ToolCard;
import it.polimi.se2018.client.network.ClientConnection;
import it.polimi.se2018.network.messages.Coordinate;
import it.polimi.se2018.network.messages.requests.*;
import it.polimi.se2018.network.messages.responses.*;
import it.polimi.se2018.client.view.ClientView;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

public class CLIClientView implements ResponseHandler, ClientView, Serializable {
    private final transient int playerID;
    private transient ClientConnection clientConnection;
    private final transient Scanner scanner;
    private transient CLIInput cliInput;
    private transient ToolCardPlayerInput toolCardPlayerInput;

    public CLIClientView(int playerID) {
        this.playerID = playerID;
        scanner = new Scanner(System.in);
        cliInput = new CLIInput(playerID);
        toolCardPlayerInput = new ToolCardPlayerInput(playerID, cliInput);
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    @Override
    //receives input from the network, called by class clientConnection
    public void handleNetworkInput(Response response) {
        response.handle(this);
    }

    @Override
    public void handleNetworkInput(Message message) {
        //not implemented client-side
    }

    //updates the board
    @Override
    //aggiorno il modelview e stampo a schermo la tua e il roundtracker
    public void handleResponse(ModelViewResponse modelViewResponse) {
        cliInput.setBoard(modelViewResponse.getModelView());
        cliInput.printDraftPool();
        checkIsYourTurn();
    }

    //prints the text message
    @Override
    //eccezioni da printare
    public void handleResponse(TextResponse textResponse) {
        cliInput.print(textResponse.getMessage());
        checkIsYourTurn();
    }

    @Override
    public void handleResponse(InputResponse inputResponse) {
        cliInput.print("Color of the die is " + inputResponse.getColor());
        int choice = cliInput.getValueDie();
        try {
            clientConnection.sendMessage(new InputMessage(playerID, choice));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    //chamo inizio turno
    public void handleResponse(TurnStartResponse turnStartResponse) {
        try {
            chooseAction();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleResponse(ToolCardResponse toolCardResponse) {
        try {
            useToolcard(toolCardResponse.getToolCardNumber());
        }
        catch (RemoteException ignored) {
        }
    }

    //Set the objective and toolcard copy to the value. Ask the window to select
    @Override
    public void handleResponse(SetupResponse setupResponse) {
        cliInput.setPlayersName(setupResponse.getPlayerNames());
        cliInput.setPrivateObjective(setupResponse.getPrivateObjective());
        cliInput.setPublicObjectives(setupResponse.getPublicObjectives());
        cliInput.setToolCards(setupResponse.getToolCards());
        cliInput.printPrivateObjective();
        cliInput.printPublicObjective();
        int windowNumber = selectWindow(setupResponse.getWindows())-1;
        cliInput.setWindow(setupResponse.getWindows().get(windowNumber));
        try {
            clientConnection.sendMessage(new SetupMessage(playerID,setupResponse.getWindows().get(windowNumber)));
        }
        catch (RemoteException e) {
        }
    }

    private void checkIsYourTurn() {
        if (playerID == cliInput.getBoard().getCurrentPlayerID()) {
            try {
                chooseAction();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else cliInput.print("It's not your turn. You can't do anything!");
    }

    private List<Integer> actionPossible() {
        List<Integer> choosable = new ArrayList<>();
        choosable.add(1);
        if (!cliInput.getBoard().hasDraftedDie()) {
            choosable.add(2);
        }
        if (cliInput.getBoard().hasDieInHand()) {
            choosable.add(3);
        }
        if (!cliInput.getBoard().hasUsedCard()) {
            choosable.add(4);
        }
        choosable.add(5);
        return choosable;
    }

    private void printActionPermitted(List<Integer> choosable) {
        for (int i : choosable) {
            if (i == 1) cliInput.print("[1] Ask information of the game");
            if (i == 2) {
                cliInput.print("[2] Draft a die");
            }
            if (i == 3) {
                cliInput.print("[3] Place the drafted die");
            }
            if (i == 4) {
                cliInput.print("[4] Select a toolcard");
            }
            if (i == 5) cliInput.print("[5] Pass turn");
        }
    }


    private void chooseAction() throws RemoteException{
        //Choose the action to do DraftDie, UseToolcard, PlaceDie, PassTurn
        int choice = -1;
        List<Integer> choosable = actionPossible();
        cliInput.print("It's your turn, choose your action");
        while (!choosable.contains(choice) || choice == 1) {
            printActionPermitted(choosable);
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    cliInput.askInformation();
                    break;
                case 2:
                    draftDie();
                    break;
                case 3:
                    placeDie();
                    break;
                case 4:
                    selectToolcard();
                    break;
                case 5:
                    passTurn();
                    break;
                default:
                    break;
            }
        }
    }

    private int selectWindow(List<Window> windows) {
        int choice = -1;
        while (choice < 1 || choice > 4) {
            cliInput.print("Select your window");
            int i = 1;
            for(Window window : windows) {
                cliInput.print("Press [" + i + "] to select this window");
                cliInput.printPlayerWindow(window.modelViewCopy());
                cliInput.print("The level of the window is " + window.getLevel());
                i++;
            }
            choice = scanner.nextInt();
        }
        return choice;
    }

    private void passTurn() throws RemoteException{
        clientConnection.sendMessage(new PassMessage(playerID));
    }

    private void draftDie() throws RemoteException {
        cliInput.print("Choose the die to draft.");
        int index = cliInput.getPositionDraftPool();
        if (index != -1) clientConnection.sendMessage(new DraftMessage(playerID, index));
        else chooseAction();
    }

    private void selectToolcard() throws RemoteException {
        int toolCard = cliInput.getToolCard();
        //if you click 3, you choose to go for another action
        if (toolCard != 3)
            clientConnection.sendMessage(new ToolCardRequestMessage(playerID, toolCard));
        else{
            try{
                chooseAction();
            }catch(RemoteException e){
                System.err.println(e.getMessage());
            }
        }
    }

    private void useToolcard(int indexOfToolCard) throws RemoteException {
        ToolCard toolCard = cliInput.getToolCards().get(indexOfToolCard);
        ToolCardMessage toolCardMessage = toolCard.handleView(toolCardPlayerInput, indexOfToolCard);
        if (!toolCardMessage.isToDismiss()) clientConnection.sendMessage(toolCardMessage);
        else chooseAction();
    }

    private void placeDie() throws RemoteException {
        cliInput.print("Choose the position where put the drafted die");
        Coordinate coordinate = cliInput.getCoordinate();
        if (!coordinate.equals(new Coordinate(-1, -1))) {
            clientConnection.sendMessage(new PlaceMessage(playerID, coordinate));
        }
        else { chooseAction(); }
    }
}