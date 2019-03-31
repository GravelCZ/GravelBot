package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import org.apache.commons.io.FilenameUtils;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZLP.Bot.Utils.Logger;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IMessage.Attachment;
import sx.blah.discord.handle.obj.IUser;

public class DeepFryerCommand implements ICommand {

	private List<String> deepfryers = new ArrayList<>();

	private List<File> emojis = new ArrayList<>();
	
	public DeepFryerCommand() {
		File emojisFolder = new File("./BotDataFolder/deepfryer/emojis/");
		if (!emojisFolder.exists()) {
			emojisFolder.mkdirs();
		}
		
		List<File> files = new ArrayList<>(Arrays.asList(emojisFolder.listFiles()));
		files.removeIf(f -> !f.getName().endsWith(".png"));
		emojis.addAll(files);
		
		File originals = new File("./BotDataFolder/deepfryer/originals/");
		if (!originals.exists()) {
			originals.mkdirs();
		}
		File deepFried = new File("./BotDataFolder/deepfryer/deepfried/");
		if (!deepFried.exists()) {
			deepFried.mkdirs();
		}
	}

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (deepfryers.contains(sender.getStringID())) {
			sendMessage(channel, ":red_circle: You are already deep frying!");
			return;
		}

		List<Attachment> attachments = msg.getAttachments();
		if (attachments.size() == 0 && args.length == 0) {
			sendMessage(channel, "This command needs to have an Image attachment with it or URL as an argument.");
			return;
		}

		deepfryers.add(sender.getStringID());
		
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					String urlTxt = null;
					if (attachments.size() != 0) {
						urlTxt = attachments.get(0).getUrl();
					} else if (args.length != 0) {
						urlTxt = args[0];
					}

					boolean isImage = Utils.isImage(urlTxt);
					if (!isImage) {
						deepfryers.remove(sender.getStringID());
						sendMessage(channel, "That is not a valid image, it needs to be .png, .jpg, .jpeg");
						return;
					}

					URL url = null;
					try {
						url = new URL(urlTxt);
					} catch (MalformedURLException e) {
						deepfryers.remove(sender.getStringID());
						sendMessage(channel, "That is not a valid URL");
						e.printStackTrace();
						return;
					}

					byte[] image = Utils.downloadFile(url, new HashMap<>());

					System.out.println("Image downloaded!");

					RenderedImage original = ImageIO.read(new ByteArrayInputStream(image));

					if (original == null) {
						deepfryers.remove(sender.getStringID());
						sendMessage(channel, sender.mention() + " i was unable to process the image.");
						return;
					}
					
					int deepFryIterations = ((original.getWidth() + original.getHeight()) / 2) / 10 ;
					
					Logger.debug("Iterations: " + deepFryIterations);
					
					String name = Utils.toB64(Utils.sha256(image));
					Logger.debug("Name: " + name);
					String suffix = FilenameUtils.getExtension(urlTxt);

					File f = new File("./BotDataFolder/deepfryer/originals/" + name + "." + suffix.replace('.', ' '));
					File f2 = new File("./BotDataFolder/deepfryer/deepfried/" + name + "-deepfried." + suffix.replace('.', ' '));

					boolean force = false;

					if (args.length >= 2) {
						if (args[1].equalsIgnoreCase("force")) {
							force = true;
						}
					}

					if (!force) {
						if (f2.exists()) {
							Logger.log("User: " + sender.getName() + " supplied an existing deep fried meme.");
							deepfryers.remove(sender.getStringID());
							sendFile(channel, f2, sender.mention());
							return;
						}
					} else {
						Logger.log("User: " + sender.getName() + " forced re-deep-fried meme: " + url.toString());
					}

					int size = image.length / (1024 * 1024);

					if (size > 2 || original.getWidth() > 3500 || original.getHeight() > 3500) {
						if (!canExecuteABigImage(sender, guild)) {
							sendMessage(channel, sender.mention() + " BOI YOUR FILE IS 2 POWERFUL!");
							deepfryers.remove(sender.getStringID());
							return;
						} else {
							sendMessage(channel, sender.mention() + " your file is a little big, might take a while for it to process.");
						}
					}

					Logger.log("Deep frying meme from: " + sender.getName() + " url: " + url.toString());

					long start = System.currentTimeMillis();

					JPEGImageWriteParam params = new JPEGImageWriteParam(null);
					params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					params.setCompressionQuality((float) (0.125 + Math.random() * 0.025));

					BufferedImage converted = new BufferedImage(original.getWidth(), original.getHeight(),
							BufferedImage.TYPE_INT_RGB);
					converted.createGraphics().drawImage((Image) original, 0, 0, Color.BLACK, null);
					
					BufferedImage img = new BufferedImage(original.getWidth(), original.getHeight(),
							BufferedImage.TYPE_INT_RGB);
					img.createGraphics().drawImage((BufferedImage) converted,
							new RescaleOp(2.3f + Utils.getRandom().nextFloat(), 1.5f, null), 0, 0);

					converted = null;
					original = null;
					
					long noise1Start = System.currentTimeMillis();

					BufferedImage noise3 = new BufferedImage(img.getWidth(), img.getHeight(),
							BufferedImage.TYPE_INT_ARGB);

					for (int x = 0; x < noise3.getWidth(); x++) {
						for (int y = 0; y < noise3.getHeight(); y++) {
							int alfa = 50;
							Color c = new Color(0, 0, 0);
							if (Utils.getRandom().nextBoolean()) {
								c = new Color(0, 0, 0, alfa);
							} else {
								c = new Color(200, 200, 200, alfa);
							}
							noise3.setRGB(x, y, c.getRGB());
						}
					}

					long noise1end = System.currentTimeMillis();
					double noise1time = (double) (noise1end - noise1Start) / 1000;

					Logger.debug("Noise 1: " + noise1time + "s");

					img.createGraphics().drawImage(noise3, 0, 0, null);

					noise3 = null;
					
					long deepfry1start = System.currentTimeMillis();

					int iteration = 0;
					while (iteration < deepFryIterations) {
						iteration++;
						ByteArrayOutputStream o = new ByteArrayOutputStream();

						ImageWriter w = ImageIO.getImageWritersByFormatName("jpg").next();

						w.setOutput(ImageIO.createImageOutputStream(o));

						w.write(null, new IIOImage(img, null, null), params);

						img = ImageIO.read(new ByteArrayInputStream(o.toByteArray()));
					}

					long deepfry1end = System.currentTimeMillis();
					double deepfry1time = (double) (deepfry1end - deepfry1start) / 1000;

					Logger.debug("1st deep frying took: " + deepfry1time + "s");

					long emojiStart = System.currentTimeMillis();
					
					BufferedImage emojisImage = null;
					
					if (emojis.size() >= 0) {
						if (!(img.getWidth() < 300 && img.getHeight() < 300)) {
							File emoji = emojis.get(Utils.getRandom().nextInt(emojis.size()));
							
							Logger.log("Using emoji: " + emoji.getPath());
							BufferedImage originalEmoji = ImageIO.read(emoji);
							
							BufferedImage tempBuffer = new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB);
							BufferedImage randomEmoji = new BufferedImage(250, 250, BufferedImage.TYPE_INT_ARGB);
							
							Graphics2D g = tempBuffer.createGraphics();
							g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
							g.drawImage(originalEmoji, 0, 0, 250, 250, null);
							g.dispose();
							
							for (int x = 0; x < tempBuffer.getWidth(); x++) {
								for (int y = 0; y < tempBuffer.getHeight(); y++) {
									Color c = new Color(tempBuffer.getRGB(x, y), true);
									int red = c.getRed();
									int green = c.getGreen();
									int blue = c.getBlue();
									int alfa = c.getAlpha();
									
									float[] hsb = Color.RGBtoHSB(red, green, blue, null);

									float hue = hsb[0];
									float saturation = hsb[1] * (float) (0.5 + Math.random());
									float brightness = hsb[2] * (float) (0.7 + Math.random() / 2);
									
									int rgb = Color.HSBtoRGB(hue, saturation, brightness);
									
									int newRed = (rgb >> 16) & 0xFF;
									int newGreen = (rgb >> 8) & 0xFF;
									int newBlue = (rgb >> 0) & 0xFF;
									
									randomEmoji.setRGB(x, y, new Color(newRed, newGreen, newBlue, alfa).getRGB());
								}
							}
							
							Logger.debug("x: " + randomEmoji.getWidth() + " y: " + randomEmoji.getHeight());
							
							BufferedImage biggerEmoji = new BufferedImage(randomEmoji.getWidth() * 2, randomEmoji.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);
							
							int x = (biggerEmoji.getWidth() - randomEmoji.getWidth()) / 2;
							int y = (biggerEmoji.getHeight()  - randomEmoji.getHeight()) / 2;
							
							biggerEmoji.createGraphics().drawImage(randomEmoji, x, y, null);
							
							int emojix = Utils.getRandom().nextInt(img.getWidth());
							int emojiy = Utils.getRandom().nextInt(img.getHeight());
							
							if ((emojix + (biggerEmoji.getWidth() / 2)) > img.getWidth()) {
								emojix = img.getWidth() - biggerEmoji.getWidth();
							}
							if ((emojiy + (biggerEmoji.getHeight() / 2)) > img.getHeight()) {
								emojiy = img.getHeight() - biggerEmoji.getHeight();
							}
							
							double rotation = Math.toRadians(Utils.getRandom().nextInt(360));
							
							AffineTransform tx = AffineTransform.getRotateInstance(rotation, biggerEmoji.getWidth() / 2, biggerEmoji.getHeight() / 2);
							AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);
							
							emojisImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
							
							emojisImage.createGraphics().drawImage(op.filter(biggerEmoji, null), emojix, emojiy, null);	
						}
					}
					
					long emojiEnd = System.currentTimeMillis();
					double emojiTime = (double) (emojiEnd - emojiStart) / 1000;
					
					Logger.debug("Emoji took: " + (double) + emojiTime + "s");
					
					long noise2start = System.currentTimeMillis();

					BufferedImage noise = new BufferedImage(img.getWidth(), img.getHeight(),
							BufferedImage.TYPE_INT_ARGB);

					for (int x = 0; x < noise.getWidth(); x++) {
						for (int y = 0; y < noise.getHeight(); y++) {
							int alfa = Utils.getRandom().nextInt(150 - 100) + 50;
							int red = Utils.getRandom().nextInt(255 - 100) + 50;
							int green = Utils.getRandom().nextInt(80 - 25) + 25;
							int blue = Utils.getRandom().nextInt(40 - 10) + 25;
							Color c = new Color(red, green, blue, alfa);
							noise.setRGB(x, y, c.getRGB());
						}
					}

					long noise2end = System.currentTimeMillis();
					double noise2time = (double) (noise2end - noise2start) / 1000;

					Logger.debug("Noise 2 time: " + noise2time + "s");

					long noise3start = System.currentTimeMillis();

					BufferedImage noise2 = new BufferedImage(img.getWidth(), img.getHeight(),
							BufferedImage.TYPE_INT_ARGB);

					for (int x = 0; x < noise2.getWidth(); x++) {
						for (int y = 0; y < noise2.getHeight(); y++) {
							int alfa = Utils.getRandom().nextInt(150 - 100) + 50;
							Color c = new Color(0, 0, 0);
							if (Utils.getRandom().nextBoolean()) {
								c = new Color(0, 0, 0, alfa);
							} else {
								c = new Color(200, 200, 200, alfa);
							}
							noise2.setRGB(x, y, c.getRGB());
						}
					}

					long noise3end = System.currentTimeMillis();
					double noise3time = (double) (noise3end - noise3start) / 1000;

					Logger.debug("Noise 3 time: " + noise3time + "s");

					Logger.debug("All noises took: " + (double) (noise1time + noise2time + noise3time) + "s");

					BufferedImage emojiAndDeepFried = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
					
					Graphics2D eadfg = emojiAndDeepFried.createGraphics();
					eadfg.drawImage(img, 0, 0, null);
					eadfg.drawImage(emojisImage, 0, 0, null);
					
					emojisImage = null;
					img = null;
					
					BufferedImage buldgeImg = new BufferedImage(emojiAndDeepFried.getWidth(), emojiAndDeepFried.getHeight(),
							BufferedImage.TYPE_INT_RGB);

					int radius = 200;

					int randomX = Utils.getRandom().nextInt(buldgeImg.getWidth());
					int randomY = Utils.getRandom().nextInt(buldgeImg.getHeight());

					Logger.debug("rX: " + randomX + " rY: " + randomY + " radius: " + radius);

					long bulgeStart = System.currentTimeMillis();

					computeBulgeImage(emojiAndDeepFried, randomX, randomY, 2.5, radius, buldgeImg);

					long bulgeEnd = System.currentTimeMillis();
					double bulgeTime = (double) (bulgeEnd - bulgeStart) / 1000;

					Logger.debug("Bulde took: " + bulgeTime + "s");
					
					BufferedImage combined = new BufferedImage(buldgeImg.getWidth(), buldgeImg.getHeight(),
							BufferedImage.TYPE_INT_RGB);

					Graphics2D g = combined.createGraphics();
					g.drawImage((Image) buldgeImg, 0, 0, null);
					g.drawImage(noise, 0, 0, null);
					g.drawImage(noise2, 0, 0, null);

					buldgeImg = null;
					noise = null;
					noise2 = null;
					
					long deepfry2start = System.currentTimeMillis();

					int iteration2 = 0;
					while (iteration2 < deepFryIterations) {
						iteration2++;
						ByteArrayOutputStream o = new ByteArrayOutputStream();

						ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

						writer.setOutput(ImageIO.createImageOutputStream(o));

						writer.write(null, new IIOImage(combined, null, null), params);

						combined = ImageIO.read(new ByteArrayInputStream(o.toByteArray()));
					}

					long deepfry2end = System.currentTimeMillis();
					double deepfry2time = (double) (deepfry2end - deepfry2start) / 1000;

					Logger.debug("2nd deep fry took: " + deepfry2time + "s");
					Logger.debug(deepFryIterations + "x JPEG compression took: " + (double) (deepfry1time + deepfry2time) + "s");

					BufferedImage finalImg = new BufferedImage(combined.getWidth(), combined.getHeight(),
							BufferedImage.TYPE_INT_RGB);
					finalImg.createGraphics().drawImage((Image) combined, 0, 0, new Color(0, 0, 0, 255), null);

					ByteArrayOutputStream b = new ByteArrayOutputStream();

					try {
						ImageIO.write(finalImg, "jpg", b);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					byte[] deepFriedImage = b.toByteArray();

					try {
						f.createNewFile();
						f2.createNewFile();

						FileOutputStream fos1 = new FileOutputStream(f2);
						fos1.write(deepFriedImage);
						fos1.flush();
						fos1.close();

						FileOutputStream fos = new FileOutputStream(f);
						fos.write(image);
						fos.flush();
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					deepfryers.remove(sender.getStringID());

					Logger.log("Original saved to: " + f.getPath());
					Logger.log("Deep fried saved to: " + f2.getPath());

					long end = System.currentTimeMillis();
					double diff = (double) (end - start) / 1000;

					Logger.debug("Deep frying took: " + (double) diff + "s");

					sendFile(channel, f2, sender.mention());
				} catch (Exception e) {
					deepfryers.remove(sender.getStringID());
					sendMessage(channel, "An internal error occured in thread: " + e.getClass().getName() + ": "+ e.getMessage());
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
				deepfryers.remove(sender.getStringID());
				sendMessage(channel, "An internal error occured in thread: " + e.getClass().getName() + ": "+ e.getMessage());
			}
		});
		t.setName("DeepFryer: " + sender.getName());
		t.start();
	}

	private void computeBulgeImage(BufferedImage input, int cx, int cy, double s, int radius, BufferedImage output) {
		int w = input.getWidth();
		int h = input.getHeight();
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int dx = x - cx;
				int dy = y - cy;
				double disSqrt = dx * dx + dy * dy;
				int sx = x;
				int sy = y;
				if (disSqrt < radius * radius) {
					double dis = Math.sqrt(disSqrt);
					double r = dis / radius;
					double a = Math.atan2(dy, dx);
					double rn = Math.pow(r, s) * dis;
					double newX = rn * Math.cos(a) + cx;
					double newY = rn * Math.sin(a) + cy;
					sx += (newX - x);
					sy += (newY - y);
				}
				if (sx >= 0 && sx < w && sy >= 0 && sy < h) {
					int rgb = input.getRGB(sx, sy);
					output.setRGB(x, y, rgb);
				}
			}
		}
	}

	public boolean canExecuteABigImage(IUser user, IGuild guild) {
		return PermissionsService.isAdmin(user, guild);
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

}
