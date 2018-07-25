// Assignment 5
// Students:
// Muaath Alaraj
// Christopher Caldwell
// Doan Dinh
// Corey Johnson
// Date: 7/24/2018

// Description: Phase 1. Load images from disk into ImageIcon array
// Set up JFrame to display JLabels containing all 57 icon images.
// Add JLabels of cards to JFrame using FlowLayout.
// Display to user.

import javax.swing.*;
import java.awt.*;

public class Assign5Phase1
{  
   // 57 cards total (52 + 4 jokers + 1 card back)
   public static final int NUM_CARD_IMAGES = 52 + 4 + 1; 
   public static Icon[] icon = new ImageIcon[NUM_CARD_IMAGES];
   
   public static void main(String[] args)
   {
      int k;
      
      // Load images from disk into icon array
      loadCardIcons();
      
      // establish main frame in which program will run
      JFrame frmMyWindow = new JFrame("Card Room");
      frmMyWindow.setSize(1150, 650);
      frmMyWindow.setLocationRelativeTo(null);
      frmMyWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      // set up layout which will control placement of buttons, etc.
      FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 5, 20);   
      frmMyWindow.setLayout(layout);
      
      // prepare the image label array
      JLabel[] labels = new JLabel[NUM_CARD_IMAGES];
      for (k = 0; k < NUM_CARD_IMAGES; k++)
         labels[k] = new JLabel(icon[k]);
      
      // place your 3 controls into frame
      for (k = 0; k < NUM_CARD_IMAGES; k++)
         frmMyWindow.add(labels[k]);

      // show everything to the user
      frmMyWindow.setVisible(true);
   }
   
   // Instantiate icons and load card images into icon array.
   private static void loadCardIcons()
   {
      final String IMAGEFOLDER = "images/";
      final String IMAGESUFFIX = ".gif";
      
      int cardIndex = 0;
      for (int suitIndex = 0; suitIndex < 4; suitIndex++)
         for (int valueIndex = 0; valueIndex < 14; valueIndex++)
         {
            String fileName = IMAGEFOLDER + intToCardValue(valueIndex)
            + intToCardSuit(suitIndex) + IMAGESUFFIX;
            icon[cardIndex] = new ImageIcon(fileName);
            cardIndex++;
         }
      
      // Finally, add card back.
      icon[cardIndex] = new ImageIcon(IMAGEFOLDER + "BK" + IMAGESUFFIX);
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
      final char[] CARDVALUES = {'A', '2', '3', '4', '5', 
                  '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'X'};
      if (valueIndex >= CARDVALUES.length || valueIndex < 0)
         return '!';
      else
         return CARDVALUES[valueIndex];
   }

}
