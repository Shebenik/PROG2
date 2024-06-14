import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.awt.Font;
import java.awt.FontMetrics;

public class Connect4 extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    private static final int sirina, visina, sirinaUnit, visinaUnit, tablaSirina, tablaVisina;
    private static JFrame okvir;
    private static Connect4 igra;
    private static Point p1, p2;
    private static JButton resetButton;

    // ustvarimo igro Štiri v vrsto
    public static void main(String[] args) {
        igra = new Connect4();
    }

    // naredimo okvir
    public Connect4() {
        setBackground(Color.WHITE);

        okvir = new JFrame("Connect 4");
        okvir.setBounds(50, 50, sirina, visina);
        okvir.add(this);
        okvir.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okvir.setVisible(true);

        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 30));
        resetButton.setBounds((sirina - 200) / 2, visina - 100, 200, 50);
        resetButton.addActionListener(e -> resetGame());
        okvir.add(resetButton);
        resetButton.setVisible(false);

        javax.swing.Timer timer = new javax.swing.Timer(10, this);
        timer.start();

        okvir.addMouseListener(this);
        okvir.addMouseMotionListener(this);
    }

    // določimo koliko stolpcev in vrstic bo imela tabla
    static {
        int zacetnasirina = 1300;
        int zacetnavisina = 1000;
        tablaSirina = 7;
        tablaVisina = 6;
        sirinaUnit = zacetnasirina / (tablaSirina + 2);
        sirina = sirinaUnit * (tablaSirina + 2);
        visinaUnit = zacetnavisina / (tablaVisina + 2);
        visina = visinaUnit * (tablaVisina + 2);
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        tabla.draw(g);
    }

    public void mouseMoved(MouseEvent e) {
        tabla.lebdenje(e.getX());
    }

    public void mousePressed(MouseEvent e) {
        if (!tabla.AliPada) {
            tabla.padanje();
        }
    }

    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}

    private void resetGame() {
        tabla.reset();
        p1 = null;
        p2 = null;
        okvir.addMouseListener(this);
        resetButton.setVisible(false);
        repaint();
    }

    static class ParaTock {
        public Point p1, p2;

        ParaTock(int x1, int y1, int x2, int y2) {
            p1 = new Point(x1, y1);
            p2 = new Point(x2, y2);
        }
    }

    static class tabla {
    	// ustvarimo tabelo
        static Color[][] tabla;
        static Color[] igralci;
        static int turn;
        static int lebdenjeX, lebdenjeY;
        static boolean igraKoncana;
        static boolean AliPada = false;

        static {
            tabla = new Color[tablaSirina][tablaVisina];
            for (Color[] barvas : tabla) {
                Arrays.fill(barvas, Color.WHITE);
            }
            // barvi igralcev (rumena, rdeča)
            igralci = new Color[]{Color.YELLOW, Color.RED};
            turn = 0;
        }

        public static void draw(Graphics g) {
        	// nariše črte
            for (int i = sirinaUnit; i <= sirina - sirinaUnit; i += sirinaUnit) {
                g.setColor(Color.BLACK);
                g.drawLine(i, visinaUnit, i, visina - visinaUnit);
                if (i == sirina - sirinaUnit) continue;
                // nariše kroge
                for (int j = visinaUnit; j < visina - visinaUnit; j += visinaUnit) {
                    g.setColor(tabla[i / sirinaUnit - 1][j / visinaUnit - 1]);
                    g.fillOval(i + 5, j + 5, sirinaUnit - 10, visinaUnit - 10);
                }
            }
            g.drawLine(sirinaUnit, visina - visinaUnit, sirina - sirinaUnit, visina - visinaUnit);
            g.setColor(Color.BLACK);
            if (p1 != null && p2 != null) {
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            // Če smo igro končali, izpiši zmagovalca
            if (igraKoncana) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                FontMetrics metrics = g.getFontMetrics(g.getFont());
                if (turn == 1) {
                	// Zmaga RUMENI
                    int x123 = (1300 - metrics.stringWidth("RUMENI ZMAGA")) / 2;
                    int y123 = ((1000 - metrics.getHeight()) / 2) + metrics.getAscent();
                    g.drawString("RUMENI ZMAGA", x123, y123);
                    int x321 = (1300 - metrics.stringWidth("Klikni za ponovno igro")) / 2;
                    g.drawString("Klikni za ponovno igro", x321, y123+50);
                } else if (turn == 0) {
                	// Zmaga RDEČI
                    int x123 = (1300 - metrics.stringWidth("RDEČI ZMAGA")) / 2;
                    int y123 = ((1000 - metrics.getHeight()) / 2) + metrics.getAscent();
                    g.drawString("RDEČI ZMAGA", x123, y123);
                    int x321 = (1300 - metrics.stringWidth("Klikni za ponovno igro")) / 2;
                    g.drawString("Klikni za ponovno igro", x321, y123+50);
                }
                // gumb za ponovno igro
                resetButton.setVisible(true);
            } else {
                g.setColor(igralci[turn]);
            }
            
         // Nekdo je zmagal, znebimo se zgornjega kroga
            g.setColor(igraKoncana ? Color.WHITE : igralci[turn]);
            g.fillOval(lebdenjeX + 5, lebdenjeY + 5, sirinaUnit - 10, visinaUnit - 10);
        }
        
     // Poskrbi za lebdenje diska nad posameznim stolpcem
        public static void lebdenje(int x) {
            x -= x % sirinaUnit;
            if (x < sirinaUnit) x = sirinaUnit;
            if (x >= sirina - sirinaUnit) x = sirina - 2 * sirinaUnit;
            lebdenjeX = x;
            lebdenjeY = 0;
        }

        public static void padanje() {
            if (AliPada || tabla[lebdenjeX / sirinaUnit - 1][0] != Color.WHITE) return;
            AliPada = true; // Počakamo, da disk pade do konca

         // Animacija diska
            new Thread(() -> {
                Color barva = igralci[turn];
                int x = lebdenjeX;
                int i;
                for (i = 0; i < tabla[x / sirinaUnit - 1].length && tabla[x / sirinaUnit - 1][i] == Color.WHITE; i++) {
                    tabla[x / sirinaUnit - 1][i] = barva;
                    try { Thread.sleep(200); } catch (Exception ignored) {}
                    tabla[x / sirinaUnit - 1][i] = Color.WHITE;
                    if (igraKoncana) return;
                }
                if (igraKoncana) return;
                tabla[x / sirinaUnit - 1][i - 1] = barva; // Krog pobarva v pravilno barvo
                checkConnect(x / sirinaUnit - 1, i - 1);

                AliPada = false; // Resetiramo, ko disk pade
            }).start();

            try { Thread.sleep(100); } catch (Exception ignored) {}
            if (igraKoncana) return;
            turn = (turn + 1) % igralci.length;
        }

     // določimo pogoj
        public static void checkConnect(int x, int y) {
            if (igraKoncana) return;

            ParaTock pair = search(tabla, x, y);

            if (pair != null) {
                p1 = new Point((pair.p1.x + 1) * sirinaUnit + sirinaUnit / 2, (pair.p1.y + 1) * visinaUnit + visinaUnit / 2);
                p2 = new Point((pair.p2.x + 1) * sirinaUnit + sirinaUnit / 2, (pair.p2.y + 1) * visinaUnit + visinaUnit / 2);
                okvir.removeMouseListener(igra);
                igraKoncana = true;
            }
        }

     // preverjamo pogoj
        public static ParaTock search(Color[][] vrsta, int i, int j) {
            Color barva = vrsta[i][j];
            int levo, desno, gor, dol;

         // vodoravni pogoj
            levo = desno = i;
            while (levo >= 0 && vrsta[levo][j] == barva) levo--;
            levo++;
            while (desno < vrsta.length && vrsta[desno][j] == barva) desno++;
            desno--;
            if (desno - levo >= 3) {
                return new ParaTock(levo, j, desno, j);
            }

         // navpični pogoj
            dol = j;
            while (dol < vrsta[i].length && vrsta[i][dol] == barva) dol++;
            dol--;
            if (dol - j >= 3) {
                return new ParaTock(i, j, i, dol);
            }

         // diagonalni pogoj
            levo = desno = i;
            gor = dol = j;
            while (levo >= 0 && gor >= 0 && vrsta[levo][gor] == barva) { levo--; gor--; }
            levo++; gor++;
            while (desno < vrsta.length && dol < vrsta[desno].length && vrsta[desno][dol] == barva) { desno++; dol++; }
            desno--; dol--;
            if (desno - levo >= 3 && dol - gor >= 3) {
                return new ParaTock(levo, gor, desno, dol);
            }

            levo = desno = i;
            gor = dol = j;
            while (levo >= 0 && dol < vrsta[levo].length && vrsta[levo][dol] == barva) { levo--; dol++; }
            levo++; dol--;
            while (desno < vrsta.length && gor >= 0 && vrsta[desno][gor] == barva) { desno++; gor--; }
            desno--; gor++;
            if (desno - levo >= 3 && dol - gor >= 3) {
                return new ParaTock(levo, dol, desno, gor);
            }

            return null;
        }

        public static void reset() {
            for (Color[] barvas : tabla) {
                Arrays.fill(barvas, Color.WHITE);
            }
            if (turn==1) {
            	turn = 1;
            } else {
            	turn = 0;
            }            
            igraKoncana = false;
            AliPada = false;
        }
    }
}