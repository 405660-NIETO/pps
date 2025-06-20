# pps

```bash
ffmpeg -f x11grab -s 3200x2000 -i :0.0 -f pulse -i default -c:v libx264 -preset fast nombre_del_video.mp4
```