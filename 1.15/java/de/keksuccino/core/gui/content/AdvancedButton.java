package de.keksuccino.core.gui.content;

import java.awt.Color;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.keksuccino.core.input.MouseInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.widget.button.Button;

public class AdvancedButton extends Button {

	private boolean handleClick = false;
	private boolean leftDown = false;
	private boolean useable = true;
	
	private Color idleColor;
	private Color hoveredColor;
	private Color idleBorderColor;
	private Color hoveredBorderColor;
	private int borderWidth = 2;
	
	public AdvancedButton(int x, int y, int widthIn, int heightIn, String buttonText, IPressable onPress) {
		super(x, y, widthIn, heightIn, buttonText, onPress);
	}
	
	public AdvancedButton(int x, int y, int widthIn, int heightIn, String buttonText, boolean handleClick, IPressable onPress) {
		super(x, y, widthIn, heightIn, buttonText, onPress);
		this.handleClick = handleClick;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			Minecraft mc = Minecraft.getInstance();
			FontRenderer font = mc.fontRenderer;
			
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			
			RenderSystem.enableBlend();
			if (!this.hasColorBackground()) {
				int i = this.getYImage(this.isHovered());
				mc.getTextureManager().bindTexture(WIDGETS_LOCATION);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
				this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
			} else {
				Color border;
				if (!isHovered) {
					IngameGui.fill(this.x, this.y, this.x + this.width, this.y + this.height, this.idleColor.getRGB());
					border = this.idleBorderColor;
				} else {
					IngameGui.fill(this.x, this.y, this.x + this.width, this.y + this.height, this.hoveredColor.getRGB());
					border = this.hoveredBorderColor;
				}
				if (this.hasBorder()) {
					//top
					IngameGui.fill(this.x, this.y, this.x + this.width, this.y + this.borderWidth, border.getRGB());
					//bottom
					IngameGui.fill(this.x, this.y + this.height - this.borderWidth, this.x + this.width, this.y + this.height, border.getRGB());
					//left
					IngameGui.fill(this.x, this.y + this.borderWidth, this.x + this.borderWidth, this.y + this.height - this.borderWidth, border.getRGB());
					//right
					IngameGui.fill(this.x + this.width - this.borderWidth, this.y + this.borderWidth, this.x + this.width, this.y + this.height - this.borderWidth, border.getRGB());
				}
			}
			
			this.renderBg(mc, mouseX, mouseY);
			
			this.drawCenteredString(font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, 14737632);
			
			RenderSystem.disableBlend();
		}

		if (this.handleClick && this.useable) {
			if (this.isHovered() && MouseInput.isLeftMouseDown() && !this.leftDown) {
				this.onClick(mouseX, mouseY);
				this.playDownSound(Minecraft.getInstance().getSoundHandler());
				this.leftDown = true;
			}
			if (!MouseInput.isLeftMouseDown()) {
				this.leftDown = false;
			}
		}
	}
	
	public void setBackgroundColor(@Nullable Color idle, @Nullable Color hovered, @Nullable Color idleBorder, @Nullable Color hoveredBorder, int borderWidth) {
		this.idleColor = idle;
		this.hoveredColor = hovered;
		this.hoveredBorderColor = hoveredBorder;
		this.idleBorderColor = idleBorder;
		
		if (borderWidth >= 0) {
			this.borderWidth = borderWidth;
		} else {
			borderWidth = 0;
		}
	}
	
	public boolean hasBorder() {
		return (this.hasColorBackground() && (this.idleBorderColor != null) && (this.hoveredBorderColor != null));
	}
	
	public boolean hasColorBackground() {
		return ((this.idleColor != null) && (this.hoveredColor != null));
	}
	
	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		if (!this.handleClick && this.useable) {
			return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
		}
		return false;
	}
	
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (this.handleClick) {
			return false;
		}
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}
	
	public void setUseable(boolean b) {
		this.useable = b;
	}
	
	public boolean isUseable() {
		return this.useable;
	}
	
	public void setHandleClick(boolean b) {
		this.handleClick = b;
	}

}
