// Assignment 5
// Students:
// Muaath Alaraj
// Christopher Caldwell
// Doan Dinh
// Corey Johnson
// Date: 7/24/2018

// Description: Phase 2. Use the GUICard class to help retrieve
// Card Icons and create JPanels. Populate the CardTable class
// with JPanels representing the human and computer card hands,
// and the play-space.

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

public class Assign5Phase2
{  
   static int NUM_CARDS_PER_HAND = 7;
   static int  NUM_PLAYERS = 2;
   static JLabel[] computerLabels = new JLabel[NUM_CARDS_PER_HAND];
   static JLabel[] humanLabels = new JLabel[NUM_CARDS_PER_HAND];  
   static JLabel[] playedCardLabels  = new JLabel[NUM_PLAYERS]; 
   static JLabel[] playLabelText  = new JLabel[NUM_PLAYERS]; 
   public static Random rand = new Random();
   
   public static void main(String[] args)
   {     
      // establish main frame in which program will run
      CardTable myCardTable 
         = new CardTable("CardTable", NUM_CARDS_PER_HAND, NUM_PLAYERS);
      myCardTable.setSize(800, 600);
      myCardTable.setLocationRelativeTo(null);
      myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // show everything to the user
      myCardTable.setVisible(false);      

      // CREATE LABELS ----------------------------------------------------
      
      // Human / Computer Card Labels
      for (int labelN = 0; labelN < NUM_CARDS_PER_HAND; labelN++)
      {
         // Human card
         Card card = generateRandomCard();
         JLabel cardLabel = new JLabel(GUICard.getIcon(card));
         humanLabels[labelN] = cardLabel;
         
         //Computer card
         cardLabel = new JLabel(GUICard.getBackCardIcon()); 
         computerLabels[labelN] = cardLabel;
      }
      
      // Human / Computer Play Area Cards
      for (int labelN = 0; labelN < NUM_PLAYERS; labelN++)
      {
         Card card = generateRandomCard();
         JLabel cardLabel = new JLabel(GUICard.getIcon(card));
         playedCardLabels[labelN] = cardLabel;
      }
      
      // Text labels
      playLabelText[0] = new JLabel( "Computer", JLabel.CENTER );
      playLabelText[1] = new JLabel( "You", JLabel.CENTER );
      
      // ADD LABELS TO PANELS -----------------------------------------
      for (int labelN = 0; labelN < NUM_CARDS_PER_HAND; labelN++)
      {
         // Add human and computer card labels to panels
         myCardTable.pnlHumanHand.add(humanLabels[labelN]);
         myCardTable.pnlComputerHand.add(computerLabels[labelN]);
      }
      
      // Add human / computer card labels to played area.
      for (int labelN = 0; labelN < NUM_PLAYERS; labelN++)
         myCardTable.pnlPlayArea.add(playedCardLabels[labelN]);
      
      // Add human / computer text labels to played area.
      for (int labelN = 0; labelN < NUM_PLAYERS; labelN++)
         myCardTable.pnlPlayArea.add(playLabelText[labelN]);

      // show everything to the user
      myCardTable.validate();
      myCardTable.repaint();
      myCardTable.setVisible(true);
   }
   
   // Returns a random card.
   static Card generateRandomCard()
   {
      char[] values = Card.getValidValues();
      char value = values[rand.nextInt(values.length)];
      Card.Suit[] suits = Card.getValidSuits();
      Card.Suit suit = suits[rand.nextInt(suits.length)];
      return new Card(value, suit);
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
      for(int index = 0; index < validValues.length; index++)
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
      final char[] CARDSUITS = {'C', 'D', 'H', 'S'};
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
                  Suit.hearts, Suit.spades,};
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
      
      return null; // No cards left in hand.
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

      for(; topCard < cards.length; topCard++)
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

