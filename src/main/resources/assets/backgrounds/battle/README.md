# Battle Backgrounds

Put battle background images in this directory and list them in `manifest.txt`.

Rules:
- One filename per line in `manifest.txt`.
- Blank lines and lines starting with `#` are ignored.
- Use wide images when possible, such as `1536x864` or `1920x1080`.
- Keep image files in `png`, `jpg`, `jpeg`, `gif`, or `bmp` format.
- The game randomly chooses one manifest entry when a new battle view is created.
- If you replace the default image, restart `mvn javafx:run` so Maven copies the updated resource.
