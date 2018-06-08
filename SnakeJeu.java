package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import jeux.Main;
import menu.MenuPrincipal;
import menu.Son;
import menu.menuBoutique.MenuBoutique;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeJeu extends Scene {
    public static final int Taille_Bloc = 40;
    public static final int Largeur = 20 * Taille_Bloc;
    public static final int Hauteur = 15 * Taille_Bloc;

    private double vitesse=0.15 ;
    private Button buttonFacile,buttonMoyen,buttonDifficile;


    private Direction direction = Direction.RIGHT;
    private boolean moved = false;
    private boolean running = false;
    private boolean snakeAlive = true;

    //Ajout des variables pour le menu pause
    private boolean pause = false ;
    private boolean canPause = true;
    private Button resumeButton;


    //Variables game over
    private Button gameOverButton;
    private Label gameOverText;
    private Label scoreLabel;

    private Button mainMenuButton;

    private Timeline timeline = new Timeline();
    public ObservableList<Node> snake;
    private Joueur joueur = new Joueur(200,0,0);
    private int scoreTemporaire=0;
    private Fruit fruitEnum;
   // Scene scene;
//======================================================================================================================
    private ControlImage controlImage;
//======================================================================================================================

    public SnakeJeu() {
        super(new Group(),800,600);
        ajouterAddAtribut();
        addKeyboardEvent();

    }

    private Parent initAttribut() {
        Pane root = new Pane();
        root.setPrefSize(Largeur, Hauteur);

        Group snakeBody = new Group();
        snake = snakeBody.getChildren();
//======================================================================================================================
        controlImage = new ControlImage(this);
        controlImage.start();
//======================================================================================================================
        Rectangle fruit = new Rectangle(
                Taille_Bloc, Taille_Bloc
        );
        fruitEnum = fruitEnum.getRandomFruit();
//        fruit.setArcWidth(20);
//        fruit.setArcHeight(20);
        fruit.setFill(new ImagePattern(fruitEnum.getImageFruit()));


        /*  Rectangle fruit = new Rectangle(Taille_Bloc, Taille_Bloc);*/




        //    fruit.setFill(fruitEnum.getCouleurFruit());
        fruit.setTranslateX((int)(Math.random() * Largeur - Taille_Bloc) / Taille_Bloc * Taille_Bloc); // les gens le : -Block_size permet de rester dans la grille si jamais
        fruit.setTranslateX((int)(Math.random() * Hauteur - Taille_Bloc) / Taille_Bloc * Taille_Bloc);

        buttonFacile = new Button();
        buttonFacile.setText("facile");
        buttonFacile.setLayoutX(350);
        buttonFacile.setLayoutY(Hauteur /2);
        buttonFacile.setOnAction(event -> {
            choixDifficultee();
            vitesse=0.20;

        });
        buttonMoyen = new Button();
        buttonMoyen.setText("normal");
        buttonMoyen.setLayoutX(350);
        buttonMoyen.setLayoutY(Hauteur /2+25);
        buttonMoyen.setOnAction(event -> {
            choixDifficultee();
            vitesse=0.15;


        });
        buttonDifficile = new Button();
        buttonDifficile.setText("difficile");
        buttonDifficile.setLayoutX(350);
        buttonDifficile.setLayoutY(Hauteur /2+50);
        buttonDifficile.setOnAction(event -> {
            choixDifficultee();
            vitesse=0.05;


        });

        KeyFrame frame = new KeyFrame(Duration.seconds(vitesse), event -> { // Si on baisse le 0.1, cela augmente la difficulté  vu que le snake ira plus vite ( oué je sais c'est logique xD )
            if(!running){
                return;
            }
            //System.out.println("vitesse :"+vitesse);

            boolean toRemove = snake.size()>1;
            Node QueuSnake = toRemove ? snake.remove(snake.size()-1) : snake.get(0); // regarder la synthaxe d'une condition ternaire sur internet puisque c'est chiant a expliquer

            double tailX = QueuSnake.getTranslateX();
            double tailY = QueuSnake.getTranslateY();
//======================================================================================================================
            switch(direction){
                case UP:
                    QueuSnake.setTranslateX(snake.get(0).getTranslateX());
                    QueuSnake.setTranslateY(snake.get(0).getTranslateY() - Taille_Bloc);
                    ((Rectangle)snake.get(0)).setFill(new ImagePattern(new Image("./skin_default/vertical.png")));
                    break;
                case DOWN :
                    QueuSnake.setTranslateX(snake.get(0).getTranslateX());
                    QueuSnake.setTranslateY(snake.get(0).getTranslateY() + Taille_Bloc);
                    ((Rectangle)snake.get(0)).setFill(new ImagePattern(new Image("./skin_default/vertical.png")));
                    break;
                case LEFT:
                    QueuSnake.setTranslateX(snake.get(0).getTranslateX()- Taille_Bloc);
                    QueuSnake.setTranslateY(snake.get(0).getTranslateY());
                    ((Rectangle)snake.get(0)).setFill(new ImagePattern(new Image("./skin_default/horizontal.png")));
                    break;
                case RIGHT :
                    QueuSnake.setTranslateX(snake.get(0).getTranslateX()+ Taille_Bloc);
                    QueuSnake.setTranslateY(snake.get(0).getTranslateY());
                    ((Rectangle)snake.get(0)).setFill(new ImagePattern(new Image("./skin_default/horizontal.png")));
                    break;
            }
            int dirQueue=0;
            /*
            1=bout a gauche
            2=bout a droite
            3=bout en bas
            4=bout en haut
             */
            System.out.println("Y : "+snake.get(0).getTranslateY());
            if (snake.size()>=2)
            {
                //normalement enlevable si le snake commence avec deux ou trois morceaux, trois étant l'idéal niveau graphique
                if (snake.get(snake.size()-2).getTranslateX()>snake.get(snake.size()-1).getTranslateX())
                    dirQueue=2;
                else if (snake.get(snake.size()-2).getTranslateX()<snake.get(snake.size()-1).getTranslateX())
                    dirQueue=3;
                else if (snake.get(snake.size()-2).getTranslateY()>snake.get(snake.size()-1).getTranslateY())
                    dirQueue=4;
                else if (snake.get(snake.size()-2).getTranslateY()<snake.get(snake.size()-1).getTranslateY())
                    dirQueue=1;
                controlImage.dirQueue=dirQueue;
            }
//======================================================================================================================
            moved = true;
            if(toRemove==true){
                snake.add(0,QueuSnake);
            }
            //collision
            for(Node rect : snake){
                if(rect != QueuSnake && QueuSnake.getTranslateX() == rect.getTranslateX() && QueuSnake.getTranslateY() == rect.getTranslateY()){
                    joueur.setScoreJoueur(scoreTemporaire);
                    if (joueur.getMeilleureScore() < scoreTemporaire){
                        joueur.setMeilleureScore(scoreTemporaire);
                    }

                    System.out.println(scoreTemporaire);
                    System.out.println(joueur.getMeilleureScore());
                    //scoreTemporaire=0;

                    gameOver();
                    break;
                }
            }
            if(QueuSnake.getTranslateX()<0 || QueuSnake.getTranslateX() >= Largeur || QueuSnake.getTranslateY() <0 || QueuSnake.getTranslateY() >= Hauteur){
                //Tu peux reprendre le score temporaire ici dans ton game over
                joueur.setScoreJoueur(scoreTemporaire);
                if (joueur.getMeilleureScore() < scoreTemporaire){
                    joueur.setMeilleureScore(scoreTemporaire);
                }
                System.out.println(scoreTemporaire);
                System.out.println(joueur.getMeilleureScore());
                //scoreTemporaire=0;

                gameOver();
            }
            if(QueuSnake.getTranslateX()==fruit.getTranslateX() && QueuSnake.getTranslateY() == fruit.getTranslateY()){
                fruit.setTranslateX((int)(Math.random() * (Largeur - Taille_Bloc)) / Taille_Bloc * Taille_Bloc);
                fruit.setTranslateY((int)(Math.random() * (Hauteur - Taille_Bloc)) / Taille_Bloc * Taille_Bloc);

                Rectangle rect = new Rectangle(Taille_Bloc, Taille_Bloc);
                //J'ajoute le score ici
                scoreTemporaire+=fruitEnum.getValeurFruit();
                fruitEnum = fruitEnum.getRandomFruit();

                fruit.setFill(new ImagePattern(fruitEnum.getImageFruit()));

                joueur.ajoutArgent(fruitEnum.getValeurFruit());

                rect.setTranslateX(tailX);
                rect.setTranslateY(tailY);
                //rect.setFill(new ImagePattern(new Image(chooseImg(rect, snake))));

                snake.add(rect);
            }
            //======================================================================================================================
            controlImage.updateCoin();
            //======================================================================================================================
        });

        //difficultée



        //creation du bouton, texte et de l'event pour le gameover
        gameOverText = new Label("Game Over ! essaye encore !");
        gameOverText.setTextFill(Color.CADETBLUE);
        gameOverText.setLayoutX(350);
        gameOverText.setLayoutY(200);
        gameOverText.setScaleX(3);
        gameOverText.setScaleY(3);

        scoreLabel = new Label();
        scoreLabel.setText("score : "+ scoreTemporaire);
        scoreLabel.setTextFill(Color.DARKRED);
        scoreLabel.setLayoutX(360);
        scoreLabel.setLayoutY(250);
        scoreLabel.setScaleX(2);
        scoreLabel.setScaleY(2);

        gameOverButton = new Button();
        gameOverButton.setText("Restart the game");
        gameOverButton.setLayoutX(350);
        gameOverButton.setLayoutY(Hauteur /2);
        gameOverButton.setOnAction(event -> {
            restartGame();
            mainMenuButton.setVisible(false);
            gameOverButton.setVisible(false);
            scoreLabel.setVisible(false);
            gameOverText.setVisible(false);
        });
        mainMenuButton = new Button();
        mainMenuButton.setText("Retour au menu principal");
        mainMenuButton.setLayoutX(350);
        mainMenuButton.setLayoutY(Hauteur /2+40);
        mainMenuButton.setOnAction(event -> {
            //mainMenuButton.setVisible(false);
            Main.getStage().setScene(new MenuPrincipal());


        });

        //creation du bouton et de l'event pour le resume
        resumeButton = new Button();
        resumeButton.setText("Resume");
        resumeButton.setLayoutX(370);
        resumeButton.setLayoutY(Hauteur /2);
        resumeButton.setOnAction(event -> {
            timeline.play();
            resumeButton.setVisible(false);
        });



        timeline.getKeyFrames().add(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);

        buttonFacile.setVisible(true);
        buttonMoyen.setVisible(true);
        buttonDifficile.setVisible(true);

        mainMenuButton.setVisible(false);
        scoreLabel.setVisible(false);
        gameOverText.setVisible(false);
        gameOverButton.setVisible(false);
        resumeButton.setVisible(false);
        root.getChildren().addAll(fruit,snakeBody,resumeButton,gameOverButton,gameOverText,scoreLabel,mainMenuButton,buttonFacile,buttonMoyen,buttonDifficile);

        return root;
    }
    public void addKeyboardEvent() {
        setRoot(initAttribut());


        setOnKeyPressed(event -> {
            if(!moved){

                return;
            }
            if(moved){
//======================================================================================================================
                /**
                 * il reste a faire :
                 * - bug de la tete (tete va a droite, appui sur D)
                 * - faire le sens de la queuep^p
                 */
                switch(event.getCode()){
                    case Z :

                        if(direction != Direction.DOWN){
                            int[] coin = new int[]{0,1,0}; //{ancienne direction, nouvelle direction, posSnake}
                            if (direction==Direction.LEFT)
                                coin[0]=3;
                            else if (direction==Direction.RIGHT)
                                coin[0]=4;
                            direction = Direction.UP;
                            controlImage.dirTete=1;
                            if(coin[0]!=0)
                                controlImage.coin.add(coin);
                        }
                        break;
                    case S :
                        if(direction != Direction.UP){
                            int[] coin = new int[]{0,2,0};
                            if (direction==Direction.LEFT)
                                coin[0]=3;
                            else if (direction==Direction.RIGHT)
                                coin[0]=4;
                            direction = Direction.DOWN;
                            controlImage.dirTete=4;
                            if(coin[0]!=0)
                                controlImage.coin.add(coin);
                        }
                        break;
                    case Q :
                        if(direction != Direction.RIGHT){
                            int[] coin = new int[]{0,3,0};
                            if (direction==Direction.UP)
                                coin[0]=1;
                            else if (direction==Direction.DOWN)
                                coin[0]=2;
                            direction = Direction.LEFT;
                            controlImage.dirTete=3;
                            if(coin[0]!=0)
                                controlImage.coin.add(coin);
                        }
                        break;
                    case D:
                        if(direction != Direction.LEFT){
                            int[] coin = new int[]{0,4,0};
                            if (direction==Direction.UP)
                                coin[0]=1;
                            else if (direction==Direction.DOWN)
                                coin[0]=2;
                            direction = Direction.RIGHT;
                            controlImage.dirTete=2;
                            if(coin[0]!=0)
                                controlImage.coin.add(coin);
                        }
                        break;
//======================================================================================================================
                    //la pause s'active quand on appuie sur "P" (comme pause quoi vu que j'avais pas d'idée)
                    case P:
                        System.out.println(pause);
                        pauseGame();
                        System.out.println(pause);
                        break;
                }
            }
            moved = false;
        });
        Main.getStage().setTitle("Snake");
//        jeux.Main.getStage().setScene(scene);
//        jeux.Main.getStage().show();
        startGame();
        timeline.pause();

    }




    private void restartGame(){
        stopGame();
        startGame();
    }
    //======================================================================================================================
    private void startGame(){
        direction= Direction.RIGHT;
        Rectangle head = new Rectangle(Taille_Bloc, Taille_Bloc);
        //head.setFill(new ImagePattern(new Image(chooseImg(head, snake))));
        snake.add(head);
        controlImage.newGame();
        timeline.play();
        running = true;
    }
    //======================================================================================================================
    private void stopGame(){
        running = false;
        timeline.stop();
        snake.clear();
    }
    public void startPause() {
        timeline.pause();
    }
    public void stopPause(){
        timeline.play();
    }
    public void pauseGame(){
        if (pause==false && canPause){
            startPause();
            resumeButton.setVisible(true);
            canPause =false;

        }
        // le if ici est pas très utile mais il m'avais aider pour corriger un bug
        if (pause==true && canPause){
            stopPause();
            pause = false;
            canPause = false;
        }
        canPause = true;
    }
    public void gameOver(){
        scoreLabel.setText("score : "+ scoreTemporaire);
        gameOverText.setVisible(true);
        scoreLabel.setVisible(true);
        gameOverButton.setVisible(true);
        mainMenuButton.setVisible(true);
        timeline.pause();
    }
    public void choixDifficultee(){
        buttonFacile.setVisible(false);
        buttonMoyen.setVisible(false);
        buttonDifficile.setVisible(false);
        timeline.play();
    }

































































    public void ajouterAddAtribut(){


    }

}