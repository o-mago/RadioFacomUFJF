package magosoftware.radiofacomufjf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;


//import wseemann.media.FFmpegMediaPlayer;

import static android.view.View.GONE;

public class RadioMain extends AppCompatActivity {

    String mms_url = "mms://radio.correios.com.br/radio";
//    String mms_url = "mms://200.17.70.162:8080/Rádio Universitária";
//    boolean botaoPlay = false;
//    boolean primeiro = true;
    Toolbar myToolbar;
//    ProgressBar progressBar;
//    ImageButton playButton;
//    ImageButton facebookButton;
//    ImageButton reload;
//    boolean reloadButton = false;
//    boolean carregando = false;
    Uri url =Uri.parse("mms://radio.correios.com.br/radio");
//    ExoPlayer player;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
//    MediaCodecAudioRenderer audioRenderer;
    private String[] mPlanetTitles;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
//    FFmpegMediaPlayerSerial mp;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_main);
        myToolbar = new Toolbar(this);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
//        mp = new FFmpegMediaPlayerSerial();
        sharedPreferences = this.getSharedPreferences("main", 0);
        editor = sharedPreferences.edit();
//        relative = findViewById(R.id.loadingPanel);
//        relative.setVisibility(View.GONE);
//        progressBar = findViewById(R.id.progressBar);
//        playButton = findViewById(R.id.playButton);
//        facebookButton = findViewById(R.id.facebook);
//        reload = findViewById(R.id.reload);
//        progressBar.setVisibility(View.INVISIBLE);

        //------------------Menu Hamburguer:---------------------------------
        mPlanetTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        Bundle bundle = new Bundle();
//        bundle.putSerializable("mediaPlayer", mp);
        Fragment fragment = new RadioFragment();
        fragment.setArguments(bundle);
        replaceFragment(fragment);

        //myToolbar.setLogo(R.drawable.ic_action_menu);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                myToolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ){

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                myToolbar.setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                myToolbar.setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
            selectItem(0);
        }
        //------------------Fim Menu Hamburger-------------------------------


//        findViewById(R.id.scroll).setSelected(true);
        //final MediaPlayer player = new MediaPlayer();

//        Uri radioUri = Uri.parse(mms_url);
//// Settings for exoPlayer
//        Allocator allocator = new DefaultAllocator(true, BUFFER_SEGMENT_SIZE);
//        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
//        DefaultDataSourceFactory dataSource = new DefaultDataSourceFactory(this, userAgent);
//        MediaCodecAudioTrackRenderer sampleSource = new MediaCodecAudioRenderer(
//                radioUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
//        audioRenderer = new MediaCodecAudioRenderer(sampleSource);
//        //player = ExoPlayerFactory.

//        final FFmpegMediaPlayer mp = new FFmpegMediaPlayer();
//        mp.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
//
//            @Override
//            public void onPrepared(FFmpegMediaPlayer mp) {
//                if(!reloadButton) {
//                    mp.start();
//                }
//                playButton.bringToFront();
//                progressBar.setVisibility(View.INVISIBLE);
//                reloadButton = false;
//                carregando = false;
//            }
//        });
//        mp.setOnErrorListener(new FFmpegMediaPlayer.OnErrorListener() {
//
//            @Override
//            public boolean onError(FFmpegMediaPlayer mp, int what, int extra) {
//                progressBar.setVisibility(View.INVISIBLE);
//                playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
//                playButton.bringToFront();
//                primeiro = true;
//                mp.reset();
//                return false;
//            }
//        });
//
//        facebookButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                intent.setData(Uri.parse("https://www.facebook.com/RadioFacomOficial/"));
//                startActivity(intent);
//            }
//        });
//
//        reload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!carregando) {
//                    try {
//                        if (mp.isPlaying()) {
//                            mp.pause();
//                            playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
//                        }
//                        if (!primeiro) {
//                            mp.reset();
//                        }
//                        mp.setDataSource(mms_url);
//                        progressBar.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
//                        progressBar.setVisibility(View.VISIBLE);
//                        progressBar.bringToFront();
//                        mp.prepareAsync();
//                        carregando = true;
//                        reloadButton = true;
//                        botaoPlay = false;
//                        if (primeiro) {
//                            primeiro = !primeiro;
//                        }
//                        //mp.pause();
//                    } catch (IllegalArgumentException e) {
//                        e.printStackTrace();
//                    } catch (SecurityException e) {
//                        e.printStackTrace();
//                    } catch (IllegalStateException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        playButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!carregando) {
//                    if (primeiro) {
//                        primeiro = !primeiro;
//                        playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
//                        try {
//                            mp.setDataSource(mms_url);
//                            progressBar.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
//                            progressBar.setVisibility(View.VISIBLE);
//                            progressBar.bringToFront();
//                            botaoPlay = !botaoPlay;
//                            carregando = true;
//                            mp.prepareAsync();
//                        } catch (IllegalArgumentException e) {
//                            e.printStackTrace();
//                        } catch (SecurityException e) {
//                            e.printStackTrace();
//                        } catch (IllegalStateException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        if (!botaoPlay || reloadButton) {
//                            playButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
//                            reloadButton = false;
//                            mp.start();
//                        } else {
//                            playButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_48dp);
//                            //mp.stop();
//                            //mp.reset();
//                            mp.pause();
//                        }
//                        botaoPlay = !botaoPlay;
//                    }
//                }
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_info).setVisible(!drawerOpen);
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_info:
                // create intent to perform web search for this planet
                replaceFragment(new Informations());
                setTitle(R.string.information);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
//        // update selected item and title, then close the drawer
        switch(position) {
            //Rádio:
            case 0:
                Bundle bundle = new Bundle();
//                bundle.putSerializable("mediaPlayer", mp);
                Fragment fragment = new RadioFragment();
                fragment.setArguments(bundle);
                replaceFragment(fragment);
                break;
                //Programação:
            case 1:
                replaceFragment(new ProgramacaoFragment());
                break;
                //Entrevistas:
            case 2:
                replaceFragmentWithArgs(new VideoList(), "PL3AbtMQ-hXBEuPhJQ1U3yJefycnwI1m-2");
                break;
                //Equipe Esportiva
            case 3:
                replaceFragmentWithArgs(new VideoList(), "PL3AbtMQ-hXBEB-ZY8p5IehB9y14xhU5b8");
                break;
                //Podcasts
            case 4:
                replaceFragmentWithArgs(new VideoList(), "PL3AbtMQ-hXBFyj2UhbjKc1KbBN7Tkl2P7");
                break;
                //Projetos da UFJF
            case 5:
                replaceFragmentWithArgs(new VideoList(), "PL3AbtMQ-hXBHTxNG1yojGqu5tHHQE-Mj5");
                break;
                //Programas Antigos
            case 6:
                replaceFragmentWithArgs(new VideoList(), "PL3AbtMQ-hXBFWqU6i0CLuNOBgQ10Ml846");
                break;
                //Rádio Novelas
            case 7:
                replaceFragmentWithArgs(new VideoList(), "PL3AbtMQ-hXBGG3118yY7b1y-7XV21l4kK");
                break;
                //Radiojornalismo
            case 8:
                replaceFragmentWithArgs(new VideoList(), "PL3AbtMQ-hXBHsmsKg4tb-u6ssZVp0ouP7");
                break;
                //Autoral Playlist
            case 9:
                //Festival da Canção
                replaceFragmentWithArgs(new VideoList(), "PL3AbtMQ-hXBGlINE9ongx_aw1-NajBaqU");
                break;
            case 10:
                replaceFragmentWithArgs(new VideoList(), "PL3AbtMQ-hXBHgLZmG9vE1PmFqYoU04CrB");
                break;
            default:
                break;
        }

        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        myToolbar.setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private void addFragment(Fragment fragment) {
        FragmentTransaction  transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private void replaceFragmentWithArgs(Fragment fragment, String playlistId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("playlistId", playlistId);
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        editor.putBoolean("carregando", false);
        editor.putBoolean("tocando", false);
        editor.commit();
        finish();
    }

    @Override
    public void onDestroy() {
        editor.putBoolean("carregando", false);
        editor.putBoolean("tocando", false);
        editor.commit();
        super.onDestroy();
    }
}
//mms://radio.correios.com.br/radio