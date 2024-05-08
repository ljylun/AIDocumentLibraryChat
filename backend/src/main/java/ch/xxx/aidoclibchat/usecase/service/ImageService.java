/**
 *    Copyright 2023 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.aidoclibchat.usecase.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import ch.xxx.aidoclibchat.domain.common.MetaData.ImageType;
import ch.xxx.aidoclibchat.domain.model.dto.ImageDto;
import ch.xxx.aidoclibchat.domain.model.dto.ImageQueryDto;

@Service
public class ImageService {
	private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
	private ChatClient chatClient;

	public ImageService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	public ImageDto queryImage(ImageQueryDto imageDto) {
		if(ImageType.JPEG.equals(imageDto.getImageType()) || ImageType.PNG.equals(imageDto.getImageType())) {
			imageDto = this.resizeImage(imageDto);
		}
		var prompt = new Prompt(new UserMessage(imageDto.getQuery(), List
				.of(new Media(MimeType.valueOf(imageDto.getImageType().getMediaType()), imageDto.getImageContent()))));
		var response = this.chatClient.call(prompt);
		var answer = response.getResult().getOutput().getContent();
		return new ImageDto(answer, Base64.getEncoder().encodeToString(imageDto.getImageContent()), imageDto.getImageType());
	}

	private ImageQueryDto resizeImage(ImageQueryDto imageDto) {
		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageDto.getImageContent()));
			int targetHeight = image.getHeight();
			int targetWidth = image.getWidth();
			if (image.getHeight() > 672 && image.getWidth() > 672) {
				if (image.getHeight() < image.getWidth()) {
					targetHeight = Math.round( image.getHeight() / (image.getHeight() / 672.0f));
					targetWidth = Math.round(image.getWidth() / (image.getHeight() / 672.0f));					
				} else {
					targetHeight = Math.round(image.getHeight() / (image.getWidth() / 672.0f));
					targetWidth = Math.round(image.getWidth() / (image.getWidth() / 672.0f));
				}
			}
			var outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
			outputImage.getGraphics().drawImage(image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH),
					0, 0, null);
			var ios = new ByteArrayOutputStream();
			ImageIO.write(outputImage, imageDto.getImageType().toString(), ios);
			imageDto.setImageContent(ios.toByteArray());
			imageDto.setContentSize(ios.toByteArray().length);
			LOG.info("Resized image to x: {}, y: {}", targetWidth, targetHeight);
		} catch (IOException e) {
			LOG.info("Image resize failed.", e);
		}
		return imageDto;
	}
}
