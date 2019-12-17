package gui;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Button {

	private boolean mouseOver = false;
	private boolean isClicked = false;
	private boolean hasClicked = false;
	private boolean mouseWasPressed = false;
	
	private PImage normalImg;
	private PImage highlightImg;
	private PImage clickedImg;
	private boolean isComposite;
	private boolean useImages;
	
	private int normalColour;
	private int highlightColour;
	private int clickedColour;
	private boolean useColour;
	private String text;
	
	private float posX;
	private float posY;
	private float width;
	private float height;
	
	private final PApplet pa;
	
	//One method for with image, one for just colour
	public Button(float posX, float posY, float width, float height, int normalColour, int highlightColour, int clickedColour, String text, final PApplet pa) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		
		this.normalColour = normalColour;
		this.highlightColour = highlightColour;
		this.clickedColour = clickedColour;
		this.text = text;
		
		useColour = true;
		
		this.pa = pa;
	}
	
	public Button(float posX, float posY, PImage buttonImg, boolean compositeImg, final PApplet pa) {
		this.posX = posX;
		this.posY = posY;
		
		if(compositeImg) {
			this.normalImg = buttonImg.get(0, 0, buttonImg.width, buttonImg.height / 3);
			this.highlightImg = buttonImg.get(0, buttonImg.height / 3, buttonImg.width, buttonImg.height / 3);
			this.clickedImg = buttonImg.get(0, 2 * buttonImg.height / 3, buttonImg.width, buttonImg.height / 3);
		}
		else {
			this.normalImg = buttonImg;
		}
		
		this.isComposite = compositeImg;
		useImages = true;
		
		this.width = normalImg.width;
		this.height = normalImg.height;

		this.pa = pa;
	}
	
	public boolean isClicked() {
		return isClicked;
	}
	
	public void setNewPos(float newX, float newY) {
		posX = newX; posY = newY;
	}
	
	public void update() {
		//isClicked and mouseOver only lasts for a frame
		isClicked = false;
		mouseOver = (pa.mouseX > posX && pa.mouseX < posX + width && pa.mouseY > posY && pa.mouseY < posY + height);
		//System.out.println(mouseOver);
		
		//If we aren't over the button anymore, reset click event
		if(!mouseOver) {
			mouseWasPressed = false;
		}
		
		//Click event - button was previously pressed but now depressed
		if(!pa.mousePressed && mouseWasPressed && mouseOver) {
			isClicked = true;
			hasClicked = !hasClicked;
			mouseWasPressed = false;
		}
		
		//Check if button is pressed
		if(pa.mousePressed) {
			mouseWasPressed = true;
		}
		display();
	}
	
	public void display() {
		if(useImages) {
			if(isComposite) {
				if(mouseOver) {
					pa.image(highlightImg, posX, posY);
				}
				else if(isClicked) {
					pa.image(clickedImg, posX, posY);
				}
				else {
					pa.image(normalImg, posX, posY);
				}
			}
			else {
				pa.image(normalImg, posX, posY);
			}
		}
		else if(useColour) {
			pa.noStroke();
			if(mouseOver) {
				pa.fill(highlightColour);
			}
			else if(hasClicked) {
				pa.fill(clickedColour);
			}
			else {
				pa.fill(normalColour);
			}
			pa.rect(posX, posY, width, height);
			pa.textAlign(PConstants.CENTER);
			pa.fill(0);
			pa.text(text, posX + width/2, posY + height/2);
		}
	}
	
	public void setHighlighted() {
		mouseOver = !mouseOver;
	}
}
