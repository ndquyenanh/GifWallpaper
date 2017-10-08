package demo.com.gifwallpaper;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by sev_user on 25-Feb-15.
 */
public class GIFWallpaperService extends WallpaperService {

    /**
     * Must be implemented to return a new instance of the wallpaper's engine.
     * Note that multiple instances may be active at the same time, such as
     * when the wallpaper is currently set as the active wallpaper and the user
     * is in the wallpaper picker viewing a preview of it as well.
     */
    @Override
    public Engine onCreateEngine() {

        try {

            Movie movie = Movie.decodeStream(getResources().getAssets().open("dv.gif"));

            return new GIFWallpaperEngine(movie);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private class GIFWallpaperEngine extends Engine {

        int frameDuration = 20;

        SurfaceHolder mHolder;

        Movie movie;
        boolean visible;
        Handler handler;

        Runnable drawGif = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        public GIFWallpaperEngine(Movie movie) {
            this.movie = movie;
            handler = new Handler();
        }

        /**
         * Called once to initialize the engine.  After returning, the
         * engine's surface will be created by the framework.
         *
         * @param surfaceHolder
         */
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            mHolder = surfaceHolder;
        }

        private void draw() {

            if (visible) {

                Canvas canvas = mHolder.lockCanvas();
                canvas.save();
                canvas.scale(3f, 3f);
                movie.draw(canvas, -100, 0);

                canvas.restore();
                mHolder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration()));

                handler.removeCallbacks(drawGif);
                handler.postDelayed(drawGif, frameDuration);
            }
        }

        /**
         * Called to inform you of the wallpaper becoming visible or
         * hidden.  <em>It is very important that a wallpaper only use
         * CPU while it is visible.</em>.
         *
         * @param visible
         */
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            this.visible = visible;

            if (visible) {
                handler.post(drawGif);
            } else {
                handler.removeCallbacks(drawGif);
            }
        }

        /**
         * Called right before the engine is going away.  After this the
         * surface will be destroyed and this Engine object is no longer
         * valid.
         */
        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGif);
        }
    }
}
