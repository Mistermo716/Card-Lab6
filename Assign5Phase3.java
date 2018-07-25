// Assignment 5
// Students:
// Muaath Alaraj
// Christopher Caldwell
// Doan Dinh
// Corey Johnson
// Date: 7/24/2018

// Description: Phase 3. Implement the High Card Game using events
// and the CardGame Framework.

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

public class Assign5Phase3
{
   static int NUM_CARDS_PER_HAND = 7;
   static int NUM_PLAYERS = 2;
   static JLabel[] computerLabels = new JLabel[NUM_CARDS_PER_HAND];
   static JLabel[] humanLabels = new JLabel[NUM_CARDS_PER_HAND];
   static JLabel[] playedCardLabels = new JLabel[NUM_PLAYERS];
   static JLabel[] playLabelText = new JLabel[NUM_PLAYERS];
   public static Random rand = new Random();

   public static void main(String[] args)
   {
      int numPacksPerDeck = 1;
      int numJokersPerPack = 0;
      int numUnusedCardsPerPack = 0;
      Card[] unusedCardsPerPack = null;

      CardGameFramework highCardGame = new CardGameFramework(
                  numPacksPerDeck, numJokersPerPack,
                  numUnusedCardsPerPack, unusedCardsPerPack,
                  NUM_PLAYERS, NUM_CARDS_PER_HAND);

      // establish main frame in which program will run
      CardTable myCardTable = new CardTable("CardTable", NUM_CARDS_PER_HAND,
                  NUM_PLAYERS);
      myCardTable.setSize(800, 600);
      myCardTable.setLocationRelativeTo(null);
      myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      myCardTable.setVisible(false);

      // Begin new game
      highCardGame.deal();
      highCardGame.sortHands();
      GameEventsHandler handler = new GameEventsHandler(highCardGame,
                  myCardTable);

      // Sets up the table GUI and adds events to GUI components.
      initTable(handler);
   }

   static void initTable(GameEventsHandler handler)
   {
      // Set up table and hands in GUI
      createLabels(handler);
      addLabelsToPanels(handler);

      // Display everything to the user
      handler.refreshView();
   }

   static void createLabels(GameEventsHandler handler)
   {
      String[] playCardNames = new String[NUM_PLAYERS];
      playCardNames[0] = GameEventsHandler.COMP_CARD;
      playCardNames[1] = GameEventsHandler.HUMAN_CARD;

      // Human / Computer Card Labels
      for (int labelN = 0; labelN < NUM_CARDS_PER_HAND; labelN++)
      {
         // Human card
         Card card = handler.game.getHand(1).inspectCard(labelN);
         JLabel cardLabel = new JLabel(GUICard.getIcon(card));
         cardLabel.setName(Integer.toString(labelN));
         handler.initPlayerCardLabel(cardLabel, card);
         humanLabels[labelN] = cardLabel;

         // Computer card
         cardLabel = new JLabel(GUICard.getBackCardIcon());
         computerLabels[labelN] = cardLabel;
      }

      // Human / Computer Play Area Cards
      for (int labelN = 0; labelN < NUM_PLAYERS; labelN++)
      {
         Card test = new Card(Card.getDefaultValue(), 
                     Card.getDefaultSuit());
         JLabel cardLabel = new JLabel(GUICard.getIcon(test));
         cardLabel.setVisible(false);
         cardLabel.setName(playCardNames[labelN]);
         playedCardLabels[labelN] = cardLabel;
      }

      // Text labels
      JLabel cpuLabel = new JLabel(GameEventsHandler.COMP_TEXT, 
                  JLabel.CENTER);
      cpuLabel.setName(GameEventsHandler.COMP_TEXT);
      playLabelText[0] = cpuLabel;

      JLabel humLabel = new JLabel(GameEventsHandler.HUMAN_TEXT, 
                  JLabel.CENTER);
      humLabel.setName(GameEventsHandler.HUMAN_TEXT);
      playLabelText[1] = humLabel;
   }

   static void addLabelsToPanels(GameEventsHandler handler)
   {
      for (int labelN = 0; labelN < NUM_CARDS_PER_HAND; labelN++)
      {
         // Add human and computer card labels to panels
         handler.table.pnlHumanHand.add(humanLabels[labelN]);
         handler.table.pnlComputerHand.add(computerLabels[labelN]);
      }

      // Add human / computer card labels to played area.
      for (int labelN = 0; labelN < NUM_PLAYERS; labelN++)
         handler.table.pnlPlayArea.add(playedCardLabels[labelN]);

      // Add human / computer text labels to played area.
      for (int labelN = 0; labelN < NUM_PLAYERS; labelN++)
         handler.table.pnlPlayArea.add(playLabelText[labelN]);
   }

}

class GameEventsHandler implements MouseListener
{
   CardGameFramework game;
   CardTable table;
   int humanScore = 0;
   int computerScore = 0;

   static final int COMP_INDEX = 0;
   static final int HUMAN_INDEX = 1;
   static final String HUMAN_CARD = "HUMAN";
   static final String COMP_CARD = "COMPUTER";
   static final String HUMAN_TEXT = "You";
   static final String COMP_TEXT = "Computer";

   public GameEventsHandler(CardGameFramework game, CardTable table)
   {
      this.game = game;
      this.table = table;
   }

   public void mouseClicked(MouseEvent e)
   {
      if (e.getComponent() == null)
         return;

      // Get clicked card label
      JLabel cardLabel = (JLabel)e.getComponent();

      // Get the hand-relative index of the card clicked.
      int cardIndex = getIndexOfCard(cardLabel);

      // remove cards human / computer hands
      table.pnlHumanHand.remove(cardIndex);
      table.pnlComputerHand.remove(0);

      // The card the player clicked.
      Card humanCard = game.playCard(HUMAN_INDEX, cardIndex);
      int humanValue = GUICard.valueAsInt(humanCard);

      // Computer picks a card
      Card computerCard = computerPlay(game.getHand(COMP_INDEX), humanValue);
      int computerValue = GUICard.valueAsInt(computerCard);

      if (humanValue > computerValue)
      {
         humanScore++;
         String msg = String.format("You win this match! ( %s )", humanScore);
         setHumanText(msg);
         setCPUText(String.format(COMP_TEXT + " ( %s )", computerScore));

      }
      else if (computerValue > humanValue)
      {
         computerScore++;
         setHumanText(String.format(HUMAN_TEXT + " ( %s )", humanScore));
         String msg = String.format(
                     "Computer wins this match. ( %s )", computerScore);
         setCPUText(msg);

      }
      else
      {
         setHumanText("TIE! Human: ( " + humanScore + " )");
         setCPUText("TIE! Computer: ( " + computerScore + " )");
      }

      displayCards(computerCard, humanCard);

      // Check for game over condition
      if (game.getHand(0).getNumCards() == 0)
      {
         setHumanText("You scored: " + humanScore);
         setCPUText("Computer scored: " + computerScore);

         JLabel winLabel = new JLabel();
         winLabel.setFont(new Font("Serif", Font.BOLD, 32));
         if (humanScore > computerScore)
            winLabel.setText("YOU WIN!!");
         else if (computerScore > humanScore)
            winLabel.setText("Damn dirty toasters! CPU WINS!");
         else
            winLabel.setText("It's a tie.");

         // Add win label to player hand panel
         table.pnlHumanHand.add(winLabel);
      }

      refreshView();
   }

   private void displayCards(Card cpuCard, Card humanCard)
   {
      JLabel cpuLabel = findLabel(table.pnlPlayArea, COMP_CARD);
      cpuLabel.setIcon(GUICard.getIcon(cpuCard));
      cpuLabel.setVisible(true);

      JLabel humanLabel = findLabel(table.pnlPlayArea, HUMAN_CARD);
      humanLabel.setIcon(GUICard.getIcon(humanCard));
      humanLabel.setVisible(true);
   }

   public void refreshView()
   {
      table.validate();
      table.repaint();
      table.setVisible(true);
   }

   // Get the index of a card label from human's hand.
   private int getIndexOfCard(JLabel label)
   {
      if (label == null)
         return -1;

      for (int i = 0; i < table.pnlHumanHand.getComponentCount(); i++)
         if (table.pnlHumanHand.getComponent(i) == label)
            return i;

      return -1;
   }

   // Returns a JLabel from a panel by name.
   private JLabel findLabel(JPanel panel, String labelName)
   {
      for (int i = 0; i < panel.getComponentCount(); i++)
      {
         Component component = panel.getComponent(i);
         if (component.getName() == labelName)
            return (JLabel)component;
      }

      return null;
   }

   private boolean setCPUText(String msg)
   {
      if (msg == null)
         return false;

      JLabel label = findLabel(table.pnlPlayArea, COMP_TEXT);
      if (label == null)
         return false;

      label.setText(msg);
      return true;
   }

   private boolean setHumanText(String msg)
   {
      if (msg == null)
         return false;

      JLabel label = findLabel(table.pnlPlayArea, HUMAN_TEXT);
      if (label == null)
         return false;

      label.setText(msg);
      return true;
   }

   public void mouseEntered(MouseEvent e)
   {
      JLabel cardLabel = (JLabel)e.getComponent();
      if (cardLabel == null)
         return;

      cardLabel.setBorder(getHighlightBorder());
   }

   public void mouseExited(MouseEvent e)
   {
      JLabel cardLabel = (JLabel)e.getComponent();
      if (cardLabel == null)
         return;

      cardLabel.setBorder(getEmptyBorder());
   }

   public void mousePressed(MouseEvent e)
   {
   }

   public void mouseReleased(MouseEvent e)
   {
   }

   void initPlayerCardLabel(JLabel cardLabel, Card card)
   {
      int cardValue = GUICard.valueAsInt(card);
      cardLabel.setBorder(GameEventsHandler.getEmptyBorder());
      cardLabel.addMouseListener(this);
      cardLabel.setName(Integer.toString(cardValue));
   }

   private Card computerPlay(Hand hand, int cardValue)
   {
      // Try to play next highest card.
      for (int k = 0; k < hand.getNumCards(); k++)
         if (GUICard.valueAsInt(hand.inspectCard(k)) > cardValue)
            return hand.playCard();

      // Computer can't beat the human card. Play lowest card.
      return hand.playCard(0);
   }

   // Class Helper Methods
   static Border getHighlightBorder()
   {
      return new BevelBorder(BevelBorder.RAISED);
   }

   static Border getEmptyBorder()
   {
      return new EmptyBorder(5, 2, 5, 2);
   }

}

class CardTable extends JFrame
{
   // Consts
   static int MAX_CARDS_PER_HAND = 56;
   static int MAX_PLAYERS = 2;

   // Member variables and accessors
   private int numCardsPerHand;

   public int getNumCardsPerHand()
   {
      return numCardsPerHand;
   }

   private int numPlayers;

   public int getNumPlayers()
   {
      return numPlayers;
   }

   public JPanel pnlComputerHand;
   public JPanel pnlHumanHand;
   public JPanel pnlPlayArea;

   public CardTable(String title, int numCardsPerHand, int numPlayers)
   {
      super(title);

      // Check argument boundaries
      if (numCardsPerHand >= 0 && numCardsPerHand <= MAX_CARDS_PER_HAND)
         this.numCardsPerHand = numCardsPerHand;
      else
         this.numCardsPerHand = 7; // Default cards per hand

      if (numPlayers >= 0 && numPlayers <= MAX_PLAYERS)
         this.numPlayers = numPlayers;
      else
         this.numPlayers = 2; // Default num players.

      pnlComputerHand = new JPanel();
      pnlPlayArea = new JPanel(new GridLayout(2, 2));
      pnlHumanHand = new JPanel();

      pnlComputerHand.setBorder(new TitledBorder("Computer Hand"));
      pnlPlayArea.setBorder(new TitledBorder("Playing Area"));
      pnlHumanHand.setBorder(new TitledBorder("Your Hand"));

      setLayout(new BorderLayout(20, 10));
      add(pnlComputerHand, BorderLayout.NORTH);
      add(pnlPlayArea, BorderLayout.CENTER);
      add(pnlHumanHand, BorderLayout.SOUTH);
   }

}

class GUICard
{
   // 14 = A thru K + joker, 4 = Clubs, Diamonds, Hearts, Spades
   private static Icon[][] iconCards = new ImageIcon[14][4];
   private static Icon iconBack; // Back of card image
   static boolean iconsLoaded = false;

   // Instantiate icons and load card images into icon array.
   private static void loadCardIcons()
   {
      final String IMAGEFOLDER = "images/";
      final String IMAGESUFFIX = ".gif";

      // Only load images once.
      if (iconsLoaded)
         return;

      for (int suitIndex = 0; suitIndex < 4; suitIndex++)
         for (int valueIndex = 0; valueIndex < 14; valueIndex++)
         {
            String fileName = IMAGEFOLDER + intToCardValue(valueIndex)
                        + intToCardSuit(suitIndex) + IMAGESUFFIX;
            iconCards[valueIndex][suitIndex] = new ImageIcon(fileName);
         }

      // Finally, add card back.
      iconBack = new ImageIcon(IMAGEFOLDER + "BK" + IMAGESUFFIX);

      // set icons loaded flag.
      iconsLoaded = true;
   }

   // gets icon for card
   // returns null on error
   static public Icon getIcon(Card card)
   {
      if (card.getErrorFlag())
         return null;

      loadCardIcons();

      return iconCards[valueAsInt(card)][suitAsInt(card)];
   }

   static public Icon getBackCardIcon()
   {
      return iconBack;
   }

   // Get int for card value.
   // Returns -1 on error.
   public static int valueAsInt(Card card)
   {
      if (card.getErrorFlag())
         return -1;

      char[] validValues = Card.getValidValues();
      for (int index = 0; index < validValues.length; index++)
         if (validValues[index] == card.getValue())
            return index;

      return -1;
   }

   // Get int for card suit.
   // Returns -1 on error.
   public static int suitAsInt(Card card)
   {
      if (card.getErrorFlag())
         return -1;

      Card.Suit[] validSuits = Card.getValidSuits();
      for (int index = 0; index < validSuits.length; index++)
         if (validSuits[index] == card.getSuit())
            return index;

      return -1;
   }

   private static char intToCardSuit(int suitIndex)
   {
      final char[] CARDSUITS = { 'C', 'D', 'H', 'S' };
      if (suitIndex >= CARDSUITS.length || suitIndex < 0)
         return '!';
      else
         return CARDSUITS[suitIndex];
   }

   private static char intToCardValue(int valueIndex)
   {
      char[] cardValues = Card.getValidValues();
      if (valueIndex >= cardValues.length || valueIndex < 0)
         return '!';
      else
         return cardValues[valueIndex];
   }

}

class Card
{
   // card values ordered from least to greatest precedence.
   private static final char[] valueRanks = { '2', '3', '4',
               '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A', 'X' };

   // Suit Enumeration
   public enum Suit
   {
      clubs, diamonds, hearts, spades
   }

   // Data section
   private boolean errorFlag;

   public boolean getErrorFlag()
   {
      return this.errorFlag;
   }

   private char value;

   public char getValue()
   {
      return value;
   }

   private void setValue(char value)
   {
      this.value = value;
   }

   private Suit suit;

   public Suit getSuit()
   {
      return suit;
   }

   private void setSuit(Suit suit)
   {
      this.suit = suit;
   }

   public boolean set(char value, Suit suit)
   {
      if (isValid(value, suit))
      {
         setValue(value);
         setSuit(suit);
         errorFlag = false;
      }
      else
      {
         errorFlag = true;
      }

      return !errorFlag;
   }

   // Constructors
   public Card()
   {
      set(getDefaultValue(), getDefaultSuit());
   }

   public Card(char value, Suit suit)
   {
      set(value, suit);
   }

   public Card(Card card)
   {
      set(card.getValue(), card.getSuit());
   }

   // Private Helper Methods

   // Check if valid card (does not examine suit)
   private boolean isValid(char value, Suit suit)
   {
      // Define all valid values.
      char[] validValues = getValidValues();

      // Iterate through all valid values testing if in set.
      // Return true if valid value in valid char array.
      for (int valId = 0; valId < validValues.length; valId++)
         if (validValues[valId] == value)
            return true;

      // return false if valid value not found.
      return false;
   }

   // returns all possible valid card values.
   public static char[] getValidValues()
   {
      return valueRanks;
   }

   public static Suit[] getValidSuits()
   {
      return new Suit[] { Suit.clubs, Suit.diamonds,
                  Suit.hearts, Suit.spades, };
   }

   public static char getDefaultValue()
   {
      return 'A';
   }

   public static Suit getDefaultSuit()
   {
      return Suit.spades;
   }

   public static Card getBadCard()
   {
      return new Card(' ', getDefaultSuit());
   }

   // Output
   public String toString()
   {
      if (!errorFlag)
         return value + " of " + suit.name();
      else
         return "** illegal **";
   }

   // Equals
   // Returns true if member values are identical to parameter member values.
   public boolean equals(Card card)
   {
      if (card == null)
         return false;
      else if (this.getValue() == card.getValue() &&
                  this.getSuit() == card.getSuit() &&
                  this.getErrorFlag() == card.getErrorFlag())
         return true;

      return false;
   }

   // Bubble sort the card array by card values.
   static void arraySort(Card[] arr, int arraySize)
   {
      if (arr == null)
         return;

      for (int i = 0; i < arraySize - 1; i++)
         for (int j = i + 1; j < arraySize; j++)
            if (GUICard.valueAsInt(arr[i]) > GUICard.valueAsInt(arr[j]))
               swap(arr, i, j);
   }

   static void swap(Card[] arr, int indexA, int indexB)
   {
      if (arr == null)
         return;

      if (indexA < 0 || indexA >= arr.length)
         return;

      if (indexB < 0 || indexB >= arr.length)
         return;

      Card temp;
      temp = arr[indexA];
      arr[indexA] = arr[indexB];
      arr[indexB] = temp;
   }
}

class Hand
{
   public final int MAX_CARDS = 50;
   private Card[] myCards;
   private int numCards;

   // default constructor
   public Hand()
   {
      numCards = 0;
      myCards = new Card[MAX_CARDS];
   }

   // remove all cards
   public void resetHand()
   {
      numCards = 0;
      myCards = new Card[MAX_CARDS];
   }

   // Adds a card to the hand - accepts a Card
   public boolean takeCard(Card card)
   {
      if (numCards >= MAX_CARDS || card.getErrorFlag())
      {
         return false;
      }
      myCards[numCards] = new Card(card.getValue(), card.getSuit());
      numCards++;
      return true;
   }

   // return and remove the top card - returns a Card
   public Card playCard()
   {
      if (numCards > 0) // Return card if exists to play.
      {
         numCards--;
         Card temp = myCards[numCards];
         myCards[numCards] = null;
         return temp;
      }

      return Card.getBadCard(); // No cards left in hand.
   }

   public Card playCard(int cardIndex)
   {
      if (cardIndex < 0 || cardIndex >= numCards)
         return Card.getBadCard();

      // Is card to play the top card?
      if (inspectCard(cardIndex) == inspectCard(numCards - 1))
         return playCard();
      else
      {
         // Bubble up card to play to last card position.
         for (int i = cardIndex; i < numCards - 1; i++)
            Card.swap(myCards, i, i + 1);

         return playCard();
      }
   }

   // returns a string consisting of entire hand
   public String toString()
   {
      String stringOfCards = "";
      for (int i = 0; i < numCards; i++)
      {
         stringOfCards += myCards[i].toString();
         if (i < numCards - 1)
         {
            stringOfCards += ", ";
         }
         if ((stringOfCards.length() % 80) > 65)
         {
            stringOfCards += "\n";
         }
      }
      return stringOfCards;
   }

   // returns the number of cards held in the hand - returns an int
   public int getNumCards()
   {
      return numCards;
   }

   // returns the specified card - accepts int - returns Card
   public Card inspectCard(int k)
   {
      if (k >= numCards || k < 0)
      {
         return Card.getBadCard();
      }
      return myCards[k];
   }

   // sorts the Card[] array by card values
   void sort()
   {
      Card.arraySort(myCards, numCards);
   }

}

class Deck
{
   public static final int NUMBER_OF_UNIQUE_CARDS = 56;
   public static final int MAX_DECKS = 6;
   public static final int MAX_CARDS = 75;
   public static Card[] masterPack = new Card[NUMBER_OF_UNIQUE_CARDS];

   private Card[] cards;
   private int topCard;
   private int numPacks;

   public Deck(int numPacks)
   {
      init(numPacks);
   }

   public Deck()
   {
      init(1);
   }

   // will initialize or reinitialize a deck to default
   public void init(int numPacks)
   {
      allocateMasterPack();

      // Enforce numPack limits.
      if (numPacks < 1)
         this.numPacks = 1;
      else if (numPacks > 6)
         this.numPacks = 6;
      else
         this.numPacks = numPacks;

      this.cards = new Card[NUMBER_OF_UNIQUE_CARDS * numPacks];
      this.topCard = 0;

      for (; topCard < cards.length; topCard++)
      {
         Card card = masterPack[topCard % NUMBER_OF_UNIQUE_CARDS];
         cards[topCard] = new Card(card);
      }
   }

   // will deal the top card
   public Card dealCard()
   {
      if (topCard <= 0)
         return Card.getBadCard();

      topCard--;
      Card card = cards[topCard];
      cards[topCard] = null;
      return card;
   }

   // Perform topCard squared number of swap operations.
   public void shuffle()
   {
      Random rand = new Random();
      for (int swapN = 0; swapN < topCard * topCard; swapN++)
      {
         Card.swap(cards, rand.nextInt(topCard), rand.nextInt(topCard));
      }
   }

   // will create master pack
   private static void allocateMasterPack()
   {
      if (masterPack[NUMBER_OF_UNIQUE_CARDS - 1] != null)
         return;

      char[] values = Card.getValidValues();
      Card.Suit[] suits = Card.getValidSuits();

      int masterIndex = 0;
      for (int suitIndex = 0; suitIndex < suits.length; suitIndex++)
         for (int valueIndex = 0; valueIndex < values.length; valueIndex++)
         {
            Card card = new Card(values[valueIndex], suits[suitIndex]);
            masterPack[masterIndex] = card;
            masterIndex++;
         }
   }

   // will return the number of packs.
   public int getNumPacks()
   {
      return this.numPacks;
   }

   // Returns number of cards remaining in deck.
   public int getNumCards()
   {
      return this.topCard;
   }

   // will inspect card at an index without removing it
   public Card inspectCard(int k)
   {
      // if k is within the length of the deck return the card
      if (k < topCard && k >= 0)
         return cards[k];

      // returns bad card to flag error.
      return Card.getBadCard();
   }

   // Adds card to deck if deck could have that
   // many of this card for number of packs the deck contains.
   public boolean addCard(Card card)
   {
      if (card == null)
         return false;

      if (topCard >= numPacks * NUMBER_OF_UNIQUE_CARDS)
         return false;

      int numFound = 0;
      for (int i = 0; i < topCard; i++)
         if (cards[i].equals(card))
            numFound++;

      if (numFound >= numPacks)
         return false;

      cards[topCard] = new Card(card);
      topCard++;

      return true;
   }

   public boolean removeCard(Card card)
   {
      if (card == null)
         return false;

      for (int i = 0; i < topCard; i++)
         if (cards[i].equals(card))
         {
            // If card is top card
            if (cards[i] == card)
               dealCard();
            else
            {
               // overwrite card with topCard
               Card cardTop = dealCard();
               cards[i] = cardTop;
            }

            return true;
         }

      return false;
   }

   // sort the cards array by value.
   public void sort()
   {
      Card.arraySort(cards, topCard);
   }

}

// class CardGameFramework ----------------------------------------------------
class CardGameFramework
{
   private static final int MAX_PLAYERS = 50;

   private int numPlayers;
   private int numPacks; // # standard 52-card packs per deck
                         // ignoring jokers or unused cards
   private int numJokersPerPack; // if 2 per pack & 3 packs per deck, get 6
   private int numUnusedCardsPerPack; // # cards removed from each pack
   private int numCardsPerHand; // # cards to deal each player
   private Deck deck; // holds the initial full deck and gets
                      // smaller (usually) during play
   private Hand[] hand; // one Hand for each player
   private Card[] unusedCardsPerPack; // an array holding the cards not used
                                      // in the game. e.g. pinochle does not
                                      // use cards 2-8 of any suit

   public CardGameFramework(int numPacks, int numJokersPerPack,
               int numUnusedCardsPerPack, Card[] unusedCardsPerPack,
               int numPlayers, int numCardsPerHand)
   {
      int k;

      // filter bad values
      if (numPacks < 1 || numPacks > 6)
         numPacks = 1;
      if (numJokersPerPack < 0 || numJokersPerPack > 4)
         numJokersPerPack = 0;
      if (numUnusedCardsPerPack < 0 || numUnusedCardsPerPack > 50) // > 1 card
         numUnusedCardsPerPack = 0;
      if (numPlayers < 1 || numPlayers > MAX_PLAYERS)
         numPlayers = 4;
      // one of many ways to assure at least one full deal to all players
      if (numCardsPerHand < 1 ||
                  numCardsPerHand > numPacks * (52 - numUnusedCardsPerPack)
                              / numPlayers)
         numCardsPerHand = numPacks * (52 - numUnusedCardsPerPack) / numPlayers;

      // allocate
      this.unusedCardsPerPack = new Card[numUnusedCardsPerPack];
      this.hand = new Hand[numPlayers];
      for (k = 0; k < numPlayers; k++)
         this.hand[k] = new Hand();
      deck = new Deck(numPacks);

      // assign to members
      this.numPacks = numPacks;
      this.numJokersPerPack = numJokersPerPack;
      this.numUnusedCardsPerPack = numUnusedCardsPerPack;
      this.numPlayers = numPlayers;
      this.numCardsPerHand = numCardsPerHand;
      for (k = 0; k < numUnusedCardsPerPack; k++)
         this.unusedCardsPerPack[k] = unusedCardsPerPack[k];

      // prepare deck and shuffle
      newGame();
   }

   // constructor overload/default for game like bridge
   public CardGameFramework()
   {
      this(1, 0, 0, null, 4, 13);
   }

   public Hand getHand(int k)
   {
      // hands start from 0 like arrays

      // on error return automatic empty hand
      if (k < 0 || k >= numPlayers)
         return new Hand();

      return hand[k];
   }

   public Card getCardFromDeck()
   {
      return deck.dealCard();
   }

   public int getNumCardsRemainingInDeck()
   {
      return deck.getNumCards();
   }

   public void newGame()
   {
      int k, j;

      // clear the hands
      for (k = 0; k < numPlayers; k++)
         hand[k].resetHand();

      // restock the deck
      deck.init(numPacks);

      // remove unused cards
      for (k = 0; k < numUnusedCardsPerPack; k++)
         deck.removeCard(unusedCardsPerPack[k]);

      // add jokers
      for (k = 0; k < numPacks; k++)
         for (j = 0; j < numJokersPerPack; j++)
            deck.addCard(new Card('X', Card.Suit.values()[j]));

      // shuffle the cards
      deck.shuffle();
   }

   public boolean deal()
   {
      // returns false if not enough cards, but deals what it can
      int k, j;
      boolean enoughCards;

      // clear all hands
      for (j = 0; j < numPlayers; j++)
         hand[j].resetHand();

      enoughCards = true;
      for (k = 0; k < numCardsPerHand && enoughCards; k++)
      {
         for (j = 0; j < numPlayers; j++)
            if (deck.getNumCards() > 0)
               hand[j].takeCard(deck.dealCard());
            else
            {
               enoughCards = false;
               break;
            }
      }

      return enoughCards;
   }

   void sortHands()
   {
      int k;

      for (k = 0; k < numPlayers; k++)
         hand[k].sort();
   }

   Card playCard(int playerIndex, int cardIndex)
   {
      // returns bad card if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1 ||
                  cardIndex < 0 || cardIndex > numCardsPerHand - 1)
      {
         // Creates a card that does not work
         return new Card('M', Card.Suit.spades);
      }

      // return the card played
      return hand[playerIndex].playCard(cardIndex);

   }

   boolean takeCard(int playerIndex)
   {
      // returns false if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1)
         return false;

      // Are there enough Cards?
      if (deck.getNumCards() <= 0)
         return false;

      return hand[playerIndex].takeCard(deck.dealCard());
   }

}
