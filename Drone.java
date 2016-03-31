/**
 * LICENCIA...: CC BY-NC-ND 4.0 | Atribucion-NoComercial-SinDerivar 4.0 Inter.
 * PROYECTO...: SIMULADOR DRON.
 * FECHA......: V.2.1.0 | 30/11/2015.
 * AUTOR......: Alfonso | www.abravogal.com
 * _____________________________________________________________________________
 *
 * TITULO.....:   SIMULADOR DRON.
 * DESCRIPCION: - Aplicación WIP creada en JAVA que permite simular el control
 *                de los motores de un drone virtual.
 */

package simulador.dron;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;


public class Drone extends Applet
{

  //____________________________________  DECLARACION DE ATRIBUTOS DEL LIENZO __

  private int      lieX, lieY;
  private Image    BF;
  private Graphics GV;
  private Dron     D;
  private Suelo3D  S;


  //______________________________________________  INICIALIZACION DEL APPLET __

  @Override
  public void init()
  {
    try
    {
      // Anchura y altura del Applet.
      this.lieX = Toolkit.getDefaultToolkit().getScreenSize().width;
      this.lieY = Toolkit.getDefaultToolkit().getScreenSize().height;
      this.setSize(new Dimension(this.lieX, this.lieY));
      this.setBackground(Color.WHITE);

      // Buffer virtual.
      this.BF = createImage(this.lieX, this.lieY);
      this.GV = this.BF.getGraphics();

      // Creacion del Drone
      this.D = new Dron(this, 4);
      this.D.inicializa();

      // Creacion del suelo
      this.S = new Suelo3D(this, this.D);

      // Mensaje de informacion.
      this.informacionUsuario();

    } catch (InterruptedException ex)
    {
      JOptionPane.showMessageDialog(null, "Fallo en INIT APPLET");
    }
  }


  //______________________________________  MENSAJE DE INFORMACION AL USUARIO __

  private void informacionUsuario()
  {
    String x;

    x = "TECLAS DE CONTROL (Clikear Applet para activarlas)";
    x += "\n------------------------------------------------------------------";
    x += "-----------------------------------------";
    x += "\n[ " + this.D.getKey()[0].toUpperCase() + " ] Motor Izquierdo Trasero.";
    x += "\n[ " + this.D.getKey()[2].toUpperCase() + " ] Motor Izquierdo Delantero.";
    x += "\n";
    x += "\n[ " + this.D.getKey()[3].toUpperCase() + " ] Motor Derecho Delantero.";
    x += "\n[ " + this.D.getKey()[1].toUpperCase() + " ] Motor Derecho Trasero.";
    x += "\n------------------------------------------------------------------";
    x += "-----------------------------------------";
    x += "\nCada motor al 100% de potencia proporciona aprox. una altura del 25%";
    x += "\nLa potencia es suministrada dejando pulsada la tecla correpondiente.";
    x += "\n\nMaximizar la pantalla y realizar un click en ella para comenzar.";
    x += "\n\n\n";

    JOptionPane.showMessageDialog(null, x);
  }


  //_____________________________________________________  VOLCADO DEL BUFFER __

  /**
   * @param g Clase Graphics para un contexto grafico.
   */
  @Override
  public void update(Graphics g)
  {
    this.GV.clearRect(0, 0, this.lieX, this.lieY);
    this.paint(this.GV);
    g.drawImage(this.BF, 0, 0, this.lieX, this.lieY, this);
  }


  //_____________________________________________________  PINTADO DEL APPLET __

  /**
   * @param g Clase Graphics para un contexto grafico.
   */
  @Override
  public void paint(Graphics g)
  {
    this.S.pintaSuelo(g);

    for (int i = 0; i < this.D.getnM(); i++)
    {
      this.D.pintaFuselaje(g, i);
      this.D.pintaMotores(g, i);
    }

    this.D.pintaDron(g);
    this.pintaInfo(g);
  }


  /**
   * @param g Clase Graphics para un contexto grafico.
   */
  private void pintaInfo(Graphics g)
  {
    int     pWt, bY, pWm, alt;
    String  nM, key;
    Color[] colores = new Color[]
    {
      Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.BLACK
    };

    pWt = bY = pWm = alt = 0;

    // Calculos sobre la informacion de los motores.
    for (int i = 0; i < this.D.getM().size(); i++)
    {
      pWm = ((this.D.getM().get(i).pW - 40) * 100) / (this.lieY / 4);
      bY  = this.lieY - this.D.getM().get(i).pW;
      key = this.D.getKey()[i].toUpperCase();
      nM  = this.D.getM().get(i).getName();

      g.setColor(colores[i]);
      g.fillRect(i * 25, bY, 25, this.D.getM().get(i).pW);

      g.setColor(Color.WHITE);
      g.fillRect(i * 25, bY - 55, 25, 55);

      g.setColor(Color.BLACK);
      g.drawString("M-" + nM,          i * 25 + 1, bY - 5);
      g.drawString("[ " + key + " ] ", i * 25 + 1, bY - 24);
      g.drawString(pWm + "%",          i * 25 + 1, bY - 44);
    }

    // Potencia total para calcular la altura.
    for (int i = 0; i < this.D.getM().size(); i++) pWt += this.D.getM().get(i).pW;

    alt = ((pWt * 100) / this.lieY);

    g.setColor(Color.BLUE);
    g.fillRect(this.lieX - 10, this.lieY - pWt, 10, pWt);

    g.setColor(Color.WHITE);
    g.fillRect(this.lieX - 75, this.lieY - pWt, 65, 15);

    g.setColor(Color.BLACK);
    g.drawString("Altura " + alt + "%", this.lieX - 75, this.lieY - pWt + 10);
  }


  //______________________________________________________  LIMTES DEL APPLET __

  public void limiteDroneX() throws InterruptedException
  {
    boolean limite = false;

    if ((this.D.getpX() < -this.D.getsMmax()))
    {
      this.D.setpX(this.lieX);
      limite = true;
    }

    if ((this.D.getpX() > this.lieX + this.D.getsMmax()))
    {
      this.D.setpX(0);
      limite = true;
    }

    if (limite) for (int i = 0; i < this.D.getM().size(); i++)
                     this.D.ajustaDron(this.D.getM().get(i));
  }


  //_____________________________________________________  GETTERS DEL APPLET __

  public int getLieX()    { return this.lieX;     }
  public int getLieY()    { return this.lieY;     }
  public int getCentroX() { return this.lieX / 2; }
  public int getCentroY() { return this.lieY / 2; }



  /** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** */
  private class Dron implements KeyListener
  {

    private ArrayList<Motor> M;
    private Drone            ctx;
    private int              nM, sM, sMmax, sMmin, pX, pY, vX, vY, W, H, led;
    private String[]         key;


    /**
     * @param ctx    Contexto grafico para dibujar.
     * @param nMotor Numero de motores.
     */
    public Dron(Drone ctx, int nMotor)
    {
      this.M     = new ArrayList<>();
      this.ctx   = ctx;
      this.nM    = nMotor;
      this.sMmax = 150;
      this.sMmin = 40;
      this.sM    = this.sMmin;
      this.W     = this.H = (this.sM * 8) / 100;
      this.pX    = this.ctx.getCentroX() - (this.W / 2);
      this.pY    = this.ctx.getCentroY() - (this.H / 2) +
                  (this.ctx.getCentroY() / 2);
      this.vX    = this.vY = 0;
      this.led   = 0;
      this.key   = new String[]
      {
        "a", "l", "s", "k"
      };

      // Asignacion de evento.
      this.ctx.addKeyListener(this);
    }


    public void inicializa() throws InterruptedException
    {
      // Creacion de Threads
      for (int i = 0; i < this.nM; i++)
      {
        this.M.add(new Motor(this.ctx, this));
        this.M.get(i).setName(String.valueOf(i));
      }

      for (int i = 0; i < this.M.size(); i++) this.M.get(i).start();

      // Establece la prespectiva.
      this.generaZoom();
    }


  //______________________________________________________  PINTADO DEL DRONE __

    /**
     * @param g Clase Graphics para un contexto grafico.
     * @param i Indice del brazo-motor a tratar.
     */
    public void pintaFuselaje(Graphics g, int i)
    {
      g.setColor(Color.BLACK);

      for (int j = 0; j < 2; j++)
      {
        g.drawLine((this.pX + (this.W / 2) + j),
                   (this.pY + (this.H / 2) + j),
                   (this.M.get(i).getpX()          +
                   (this.M.get(i).getW() / 2) + j),
                   (this.M.get(i).getpY()          +
                   (this.M.get(i).getH() / 2) + j));
      }
    }


    /**
     * @param g Clase Graphics para un contexto grafico.
     * @param i Indice del motor a tratar.
     */
    public void pintaMotores(Graphics g, int i)
    {
      g.setColor(Color.BLACK);
      g.fillOval((this.M.get(i).getpX() - 4),
                 (this.M.get(i).getpY() - 4),
                 (this.M.get(i).getW()  + 8),
                 (this.M.get(i).getH()  + 8));

      switch (i) // Color para los motores.
      {
        case 0: case 1: g.setColor(Color.GRAY);      break;
        case 2: case 3: g.setColor(Color.DARK_GRAY); break;
      }

      g.fillOval(this.M.get(i).getpX(), this.M.get(i).getpY(),
                 this.M.get(i).getW(),  this.M.get(i).getH());
    }


    /**
     * @param g Clase Graphics para un contexto grafico.
     */
    public void pintaDron(Graphics g)
    {
      // Bola principal.
      g.setColor(Color.WHITE);
      g.fillOval((this.pX - this.W),
                 (this.pY - this.H),
                 (this.W  + this.W * 2),
                 (this.H  + this.H * 2));

      // Contorno de la bola principal.
      g.setColor(Color.BLACK);
      g.drawOval((this.pX - this.W),
                 (this.pY - this.H),
                 (this.W  + this.W * 2),
                 (this.H  + this.H * 2));

      // Led de posicion.
      g.setColor(Color.WHITE);
      this.led++;

      // Control del led.
      if(this.led % 10 == 0) { g.setColor(Color.RED); this.led = 0;}
      g.fillOval(this.pX, this.pY, this.W, this.H);
    }


  //__________________________________________________  METODOS SINCRONIZADOS __

    /**
     * @param m Indice del motor a tratar.
     */
    private synchronized void ajustaDron(Motor m) throws InterruptedException
    {
      int lieY = this.ctx.getLieY(), dY = this.pY, dX = this.pX, sM = this.sM;
      int pEjeX = 0, pEjeY = 0, vEjeX = 0, vEjeY = 0;

      switch (m.getName()) // Ajuste de parametros.
      {
        case "0":
          pEjeX = (sM) * (-1);
          pEjeY = ((dY * sM) / (lieY)) - (sM / 2);

          vEjeX = (sM);
          vEjeY = ((dY * sM) / lieY) - (sM / 2);

          break;
        case "1":
          pEjeX = (sM);
          pEjeY = ((dY * sM) / lieY) - (sM / 2);

          vEjeX = (sM);
          vEjeY = ((dY * sM) / lieY) - (sM / 2);

          break;
        case "2":
          pEjeX = (sM / 2) * (-1);
          pEjeY = (((dY * (sM / 2)) / lieY) - (sM / 4)) * (-1);

          vEjeX = (sM / 2);
          vEjeY = ((dY * (sM / 2)) / lieY) - (sM / 4);

          break;
        case "3":
          pEjeX = (sM / 2);
          pEjeY = (((dY * (sM / 2)) / lieY) - (sM / 4)) * (-1);

          vEjeX = (sM / 2);
          vEjeY = ((dY * (sM / 2)) / lieY) - (sM / 4);

          break;
      }

      // Ajusta la vision del Drone al llegar al centro de la pantalla.
      if (dY < this.ctx.getCentroY()) vEjeY *= (-1);

      // Nuevos parametros de la estructura.
      m.setW(vEjeX);
      m.setH(vEjeY);

      m.setpX((dX + (pEjeX)) - (m.getW() / 2));
      m.setpY((dY + (pEjeY)) - (m.getH() / 2));

    }


    /**
     * @param m Indice del motor a tratar.
     */
    public synchronized void sincronizaMotores(Motor m) throws InterruptedException
    {
      // Despierta los Threads.
      notifyAll();

      int[] motores = new int[]
      {
        0, 0, 0, 0
      };

      for (int i = 0; i < motores.length; i++) motores[i] = this.M.get(i).getpW();

      if (this.calculaPotencia(motores) > 0)
      {
        this.asignaVelocidad(motores);
        this.calculaAltura(this.calculaPotencia(motores));
        this.generaZoom();
      }
      else estabilizaDron(motores);

      for (int i = 0; i < this.getM().size(); i++)
           this.ajustaDron(this.getM().get(i));

      if(m.getpW() <= 0) wait();
    }


  //_____________________________________________________  METODOS DE CALCULO __

    /**
     * @param motores Array con la potencia de los motores.
     * @return        Calculo de la potencia generada.
     */
    private int calculaPotencia(int[] motores)
    {
      return motores[0] + motores[2] + Math.abs(motores[1] + motores[3]);
    }


    /**
     * @param motores Array con la potencia de los motores.
     */
    private void asignaVelocidad(int[] motores)
    {
      this.vX = 2;
      if (Math.abs(motores[1] + motores[3]) < (motores[0] + motores[2]))
          this.vX = -2;
    }


    private void generaZoom()
    {
      if (this.pY <= (this.ctx.getCentroY() + (this.ctx.getCentroY() / 2)))
          this.sM = (this.pY * this.sMmin) / this.sMmax;

      // Zoom de la cabeza del Drone.
      this.W = this.H = (this.sM * 8) / 100;
    }


    /**
     * @param tW Potencia generada para calcular la altura.
     */
    private void calculaAltura(int tW)
    {
      this.pY = -(tW - (this.ctx.getCentroY()) - (this.ctx.getCentroY() / 2));
    }


    /**
     * @param motores Array con la potencia de los motores.
     */
    private void estabilizaDron(int[] motores)
    {
      if (Math.abs(motores[1] + motores[3]) == (motores[0] + motores[2]))
      {
        this.vX = 0;
        this.pX = this.numRandom(this.pX - 2, this.pX + 2);
        this.pY = this.numRandom(this.pY - 1, this.pY + 1);
      }
    }


    /**
     * @param i Indice del motor a tratar.
     */
    private void asignaPotenciaMotor(int i)
    {
      if (this.M.get(i).getpW() <= this.M.get(i).getpWm())
          this.M.get(i).setpW(this.M.get(i).getpW() + 10);
    }


    /**
     * @param min Numero entero inferior.
     * @param max Numero entero superior.
     * @return    Numero aleatorio entre el rango inferior y superior.
     */
    private int numRandom(int min, int max)
    {
      return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }


  //_____________________________________________________  EVENTOS DE TECLADO __

    /**
     * @param e Objeto que genero el evento (tecla).
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
      for (int i = 0; i < this.M.size(); i++)
           if (String.valueOf(e.getKeyChar()).equals(this.key[i]))
               asignaPotenciaMotor(i);
    }


    /**
     * @param e Objeto que genero el evento (tecla).
     */
    @Override
    public void keyTyped(KeyEvent e) {}


    /**
     * @param e Objeto que genero el evento (tecla).
     */
    @Override
    public void keyReleased(KeyEvent e) {}


  //_____________________________________________  GETTERS Y SETTERS DEL DRON __

    public int getW()                    { return this.W;      }
    public void setW(int W)              { this.W = W;         }

    public int getH()                    { return this.H;      }
    public void setH(int H)              { this.H = H;         }

    public int getpX()                   { return this.pX;     }
    public void setpX(int pX)            { this.pX = pX;       }

    public int getpY()                   { return this.pY;     }
    public void setpY(int pY)            { this.pY = pY;       }

    public int getvX()                   { return this.vX;     }
    public void setvX(int vX)            { this.vX = vX;       }

    public int getvY()                   { return this.vY;     }
    public void setvY(int vY)            { this.vY = vY;       }

    public int getnM()                   { return this.nM;     }
    public void setnM(int nM)            { this.nM = nM;       }

    public int getsM()                   { return this.sM;     }
    public void setsM(int nM)            { this.sM = nM;       }

    public int getsMmax()                { return this.sMmax;  }
    public void setsMmax(int sMmax)      { this.sMmax = sMmax; }

    public int getsMmin()                { return this.sMmin;  }
    public void setnMmin(int nM)         { this.sMmin = sMmin; }

    public ArrayList<Motor> getM()       { return this.M;      }
    public void setM(ArrayList<Motor> M) { this.M = M;         }

    public String[] getKey()             { return this.key;    }
    public void setKey(String[] key)     { this.key = key;     }

    public int getLed()                  { return this.led;    }
    public void setLed(int led)          { this.led = led;     }




    /** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** */
    private class Motor extends Thread
    {

      private Drone ctx;
      private Dron  D;
      private int   W, H, pX, pY, pW, pWm;


      /**
       * @param ctx  Contexto grafico para dibujar.
       * @param dron Objeto Dron.
       */
      public Motor(Drone ctx, Dron dron)
      {
        this.ctx = ctx;
        this.D   = dron;
        this.pW  = this.W = this.H = 0;
        this.pWm = (this.ctx.getCentroY() / 2) + this.D.getsMmin();
      }


      @Override
      public void run()
      {
        while (true)
        {
          try
          {
            this.ctx.repaint();
            this.D.ajustaDron(this);
            this.D.sincronizaMotores(this);

            if (this.pW > 0) this.pW--;

            this.D.pX += this.D.vX;

            this.ctx.limiteDroneX();

            Thread.sleep(24);
          } catch (InterruptedException ex)
          {
            JOptionPane.showMessageDialog(null, "FALLO MOTOR " + this.getName());
          }
        }
      }



  //____________________________________________  GETTERS Y SETTERS DEL MOTOR __

      public int getW()           { return this.W;   }
      public void setW(int W)     { this.W = W;      }

      public int getH()           { return this.H;   }
      public void setH(int H)     { this.H = H;      }

      public int getpX()          { return this.pX;  }
      public void setpX(int pX)   { this.pX = pX;    }

      public int getpY()          { return this.pY;  }
      public void setpY(int pY)   { this.pY = pY;    }

      public int getpW()          { return this.pW;  }
      public void setpW(int pW)   { this.pW = pW;    }

      public int getpWm()         { return this.pWm; }
      public void setpWm(int pWm) { this.pWm = pWm;  }

    }

  }




  /** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
   ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** */
  private class Suelo3D
  {

    private Drone   ctx;
    private Dron     D;
    private Graphics g;
    private int      perspectiva;
    private double   opticaX;
    private double   opticaY;
    private double   nOpticaY;
    private float    alpha;


    /**
     * @param ctx  Contexto grafico para dibujar.
     * @param dron Objeto Dron.
     */
    public Suelo3D(Drone ctx, Dron dron)
    {
      this.ctx         = ctx;
      this.D           = dron;
      this.perspectiva = 0;
      this.opticaX     = 1.5;
      this.opticaY     = 18;
      this.nOpticaY    = 0;
      this.alpha       = (float) 0.1;
    }


    /**
     * @param g Clase Graphics para un contexto grafico.
     */
    public void pintaSuelo(Graphics g)
    {
      this.perspectiva = this.D.getsM() - (this.ctx.getCentroY() / 2);
      this.nOpticaY    = 0;
      this.alpha       = (float) 0.1;
      this.g           = g;
      this.opticaX     = 1.5;
      this.ejeX();
      this.ejeY();
    }


    private void ejeX()
    {
      for (int i = 0; i < this.ctx.getCentroY() + this.D.pY; i += 10)
      {
        if (this.alpha < 1) this.alpha += 0.05;

        this.g.setColor(new Color(0, 0, 0, this.alpha));
        i *= this.opticaX;

        this.g.drawLine( 0,
                        (this.ctx.getCentroY() + i - this.perspectiva),
                         this.ctx.getLieX(),
                        (this.ctx.getCentroY() + i - this.perspectiva));
      }
    }


    private void ejeY()
    {
      this.g.setColor(Color.LIGHT_GRAY);
      this.g.drawLine(this.ctx.getCentroX(),
                      this.ctx.getCentroY() - this.perspectiva,
                      this.ctx.getCentroX(),
                      this.ctx.getLieY());

      for (int i = 40; i < this.ctx.getCentroX(); i += 40)
      {
        this.nOpticaY = (int) (this.opticaY * i);

        this.g.drawLine((this.ctx.getCentroX() - i),
                         this.ctx.getCentroY() - this.perspectiva,
                        (this.ctx.getCentroX() - (int) this.nOpticaY),
                         this.ctx.getLieY());

        this.g.drawLine((this.ctx.getCentroX() + i),
                         this.ctx.getCentroY() - this.perspectiva,
                        (this.ctx.getCentroX() + (int) this.nOpticaY),
                         this.ctx.getLieY());
      }
    }

  }

}