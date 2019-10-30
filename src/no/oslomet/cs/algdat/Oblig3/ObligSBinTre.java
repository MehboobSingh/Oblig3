package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import java.util.*;

public class ObligSBinTre<T> implements Beholder<T>
{
  private static final class Node<T>   // en indre nodeklasse
  {
    private T verdi;                   // nodens verdi
    private Node<T> venstre, høyre;    // venstre og høyre barn
    private Node<T> forelder;          // forelder

    // konstruktør
    private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder)
    {
      this.verdi = verdi;
      venstre = v; høyre = h;
      this.forelder = forelder;
    }

    private Node(T verdi, Node<T> forelder)  // konstruktør
    {
      this(verdi, null, null, forelder);
    }

    @Override
    public String toString(){ return "" + verdi;}

  } // class Node

  private Node<T> rot;                            // peker til rotnoden
  private int antall;                             // antall noder
  private int endringer;                          // antall endringer

  private final Comparator<? super T> comp;       // komparator

  public ObligSBinTre(Comparator<? super T> c)    // konstruktør
  {
    rot = null;
    antall = 0;
    comp = c;
  }

//Oppgave 1

  @Override
  public boolean leggInn(T verdi){
    Objects.requireNonNull(verdi, "Null verdier ikke tillatt!");

    Node<T> p = rot;
    Node<T> q = null;

    int cmp = 0;

    while(p != null) {
      q = p;
      cmp = comp.compare(verdi, p.verdi);
      p = cmp < 0 ? p.venstre:p.høyre;
    }

    p = new Node<>(verdi, q);

    if(q == null) rot = p;
    else if(cmp < 0) q.venstre = p;
    else q.høyre = p;

    antall++;
    endringer++;

    return true;
  }


  //Oppgve 2

  @Override
  public boolean inneholder(T verdi) {
    if (verdi == null)
      return false;

    Node<T> p = rot;

    while (p != null) {
      int cmp = comp.compare(verdi, p.verdi);
      if (cmp < 0)
        p = p.venstre;
      else if (cmp > 0)
        p = p.høyre;
      else
        return true;
    }

    return false;
  }

  @Override
  public int antall()   {
    return antall;
  }

  @Override
  public boolean tom(){
    return antall == 0;
  }

  public int antall(T verdi)   {

    Node<T> p = rot;
    int n = 0;

    while(p != null) {
      int cmp = comp.compare(verdi, p.verdi);

      if(cmp < 0) p = p.venstre;
      else if(cmp > 0) p = p.høyre;
      else {
        p = p.høyre;
        n++;
      }
    }

    return n;
  }


  //Oppgave 3
  private static boolean venstreTre = true;

  private static <T> Node<T> nesteInorden(Node<T> p) {
    // p.forelder er ulik null dersom p ikke er rot-noden.
    if(p.forelder != null || !venstreTre) {

      if(p.høyre != null)
        p = p.høyre;
      else {
        Node<T> q = p.forelder;

        while(q != null && p == q.høyre) {
          p = q;
          q = q.forelder;
        }
        if(q != null) venstreTre = false;
        else venstreTre = true;

        return q;
      }
    } else if(p.venstre == null)	// Dersom rot-noden ikke har venstre barn, returnerer vi p og sjekker høyre subtre neste gang.
      venstreTre = false;

    while(p.venstre != null) p = p.venstre;

    return p;
  }

  @Override
  public String toString() {
    StringJoiner stringJoiner = new StringJoiner(", ", "[", "]");

    Node<T> p = null;

    //ObligSBinTre.venstreSubtre = true; // nesteInorden()
    if(rot != null) p = nesteInorden(rot);

    while(p != null ) {
      stringJoiner.add(p.verdi.toString());
      p = nesteInorden(p);
    }

    return stringJoiner.toString();
  }


  //Oppgave 4

  public String omvendtString() {
    StringJoiner stringJoiner = new StringJoiner(", ", "[", "]");
    Deque<Node<T>> stakk = new ArrayDeque<>();
    Node<T> p = rot;

    if(tom()) return stringJoiner.toString();

    while(p.høyre != null) {
      stakk.push(p);
      p = p.høyre;
    }

    while(true) {
      stringJoiner.add(p.toString());

      if(p.venstre != null) {
        p = p.venstre;

        while(p.høyre != null) {
          stakk.push(p);
          p = p.høyre;
        }
      } else if(!stakk.isEmpty())
        p = stakk.pop();
      else break;
    }

    return stringJoiner.toString();
  }






  @Override
  public boolean fjern(T verdi) {
    if(verdi == null) return false;

    Node<T> p = rot;
    Node<T> q = null;

    while(p != null) {
      int cmp = comp.compare(verdi, p.verdi);

      if(cmp < 0) {
        q = p;
        p = p.venstre;
      } else if(cmp > 0) {
        q = p;
        p = p.høyre;
      } else
        break;
    }

    if(p == null) return false;

    if(p.venstre == null || p.høyre == null) {
      Node<T> b = p.venstre != null ? p.venstre : p.høyre;

      if(p == rot) {
        rot = b;
        if(b != null)
          b.forelder = null;
      } else if(q.venstre == p) {
        q.venstre = b;
        if(b != null)
          b.forelder = q;
      } else {
        q.høyre = b;
        if(b != null)
          b.forelder = q;
      }

      p.forelder = p.venstre = p.høyre = null;
      p.verdi = null;
    } else {
      Node<T> r = p.høyre;
      Node<T> s = p;

      while(r.venstre != null) {
        s = r;
        r = r.venstre;
      }

      p.verdi = r.verdi;

      if(s != p)
        s.venstre = r.høyre;
      else
        s.høyre = r.høyre;

      if(r.høyre != null)
        r.høyre.forelder = s;

      r.forelder = r.høyre = null;
      r.verdi = null;
    }

    antall--;
    endringer++;

    return true;
  }



  public int fjernAlle(T verdi) {

    Deque<Node<T>> kø = new ArrayDeque<>();
    Node<T> p = rot;
    int n = 0;

    while(p != null) {
      int cmp = comp.compare(verdi, p.verdi);

      if(cmp < 0)
        p = p.venstre;
      else if(cmp > 0)
        p = p.høyre;
      else {
        // Vi lagrer alle referanser som er lik verdien vi ønsker på.
        kø.push(p);
        p = p.høyre;
      }
    }

    // Dersom inneholdet i kø er større enn 0, har vi funnet verdi(er) i treet.
    while(kø.size() > 0) {
      p = kø.pop();

      Node<T> q = p.forelder;

      if(p.venstre == null || p.høyre == null) {
        Node<T> b = p.venstre != null ? p.venstre : p.høyre;

        if(p == rot) {
          rot = b;
          if(b != null)
            b.forelder = null;
        } else if(q.venstre == p) {
          q.venstre = b;
          if(b != null)
            b.forelder = q;
        } else {
          q.høyre = b;
          if(b != null)
            b.forelder = q;
        }

        p.forelder = p.venstre = p.høyre = null;
        p.verdi = null;
      } else {
        Node<T> r = p.høyre;
        Node<T> s = p;

        while(r.venstre != null) {
          s = r;
          r = r.venstre;
        }

        p.verdi = r.verdi;

        if(s != p)
          s.venstre = r.høyre;
        else
          s.høyre = r.høyre;

        if(r.høyre != null)
          r.høyre.forelder = s;

        r.forelder = r.høyre = null;
        r.verdi = null;
      }

      antall--;
      n++;
    }

    endringer++;
    return n;
  }


  @Override
  public void nullstill() {
    if(rot != null) {

      nullstill(rot);

      rot = null;
      antall = 0;
      endringer++;
    }
  }
  //Oppgave 6

  private void nullstill(Node<T> p) {
    if(p.venstre != null) nullstill(p.venstre);
    if(p.høyre != null) nullstill(p.høyre);

    p.venstre = null;
    p.høyre = null;
    p.verdi = null;
    p.forelder = null;
  }

  public String høyreGren() {
    StringJoiner sj = new StringJoiner(", ", "[", "]");
    Node<T> p = rot;

    if(p == null) return sj.toString();

    while(true) {
      while(p.høyre != null) {
        sj.add(p.verdi.toString());
        p = p.høyre;
      }

      sj.add(p.verdi.toString());

      if(p.venstre != null) p = p.venstre;
      else break;
    }

    return sj.toString();
  }

  public String lengstGren() {


    // Alternativ metode som benytter seg av iterasjon.
    Node<T> dypesteNode = rot;
    Node<T> p = rot;
    int dybde = 0;
    int i = 0;

    if(p == null) return "[]";

    while(true) {
      // p.venstre == p.høyre hvis og bare hvis begge referansene er null.
      // p er da en bladnode, ellers er p en indre node.

      if(p.venstre == p.høyre) {

        // Vi sjekker n� om den dybden vi har f�tt til bladnoden er st�rre enn den vi oppn�dde sist.
        if(i > dybde) {
          dypesteNode = p;
          dybde = i;
        }

        Node<T> q = p.forelder;

        // Vi leter oss s� bakover til f�rste node som f�rer til neste gren.
        while(q != null && (q.høyre == p || q.høyre == null)) {
          p = q;
          q = q.forelder;
          i--;
        }

        // Dersom q er null, har vi traversjert igjennom hele treet.
        if(q == null) {
          StringJoiner sj = new StringJoiner(", ", "[", "]");
          Deque<T> stakk = new ArrayDeque<>();

          p = dypesteNode;
          while(p != null) {
            stakk.push(p.verdi);
            p = p.forelder;
          }

          while(!stakk.isEmpty())
            sj.add(stakk.pop().toString());

          return sj.toString();
        }

        p = q.høyre;

      } else {
        while(p.venstre != null) {
          p = p.venstre;
          i++;
        }

        if(p.høyre != null) {
          p = p.høyre;
          i++;
        }
      }
    }
  }


//Oppgave 7

  public String[] grener() {
    Queue<String> grener = new ArrayDeque<>();
    Queue<T> gren = new ArrayDeque<>();
    Node<T> p = rot;

    if(p == null) return new String[] {};

    while(true) {
      if(p.venstre == p.høyre) {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        Node<T> q = p.forelder;
        int i = 1; 				// Vi skal g� minst ett niv� tilbake i treet s� bladnoden ikke blir tatt med flere ganger.

        gren.add(p.verdi);		// Legger til bladnoden i grenen.

        while(q != null && (q.høyre == p || q.høyre == null)) {
          p = q;
          q = q.forelder;
          i++;
        }

        // Tar ut grenens verdier, legger til i en StringJoiner, og legger s� verdien tilbake i køen igjen.
        // "gren.size() - i" vil være de nivåene som skal inn i den nye grenen.
        for(int j = 0; j < gren.size() - i; j++) {
          T verdi = gren.poll();
          sj.add(verdi.toString());
          gren.add(verdi);
        }

        // Tar ut de verdiene som ikke tilh�rer andre enn grenen vi jobber p�.
        while(i > 0) {
          sj.add(gren.poll().toString());
          i--;
        }

        grener.add(sj.toString());

        if(q == null) {
          String buffer[] = new String[grener.size()];
          int j = 0;

          while(!grener.isEmpty()) buffer[j++] = grener.remove();

          return buffer;
        }

        p = q.høyre;

      } else {
        while(p.venstre != null) {
          gren.add(p.verdi);
          p = p.venstre;
        }

        if(p.høyre != null) {
          gren.add(p.verdi);
          p = p.høyre;
        }
      }
    }
  }



  //Oppgave 8

  private String blad(Node<T> p) {
    if(p.venstre == p.høyre)
      return p.verdi.toString();
    else if(p.venstre != null && p.høyre != null)
      return blad(p.venstre) + ", " + blad(p.høyre);
    else if(p.venstre != null)
      return blad(p.venstre);
    else
      return blad(p.høyre);
  }

  // Oppgave (8.a)
  public String bladnodeverdier() {
    StringJoiner sj = new StringJoiner(", ", "[", "]");

    if(rot == null) return sj.toString();

    sj.add(blad(rot));

    return sj.toString();
  }

  // Oppgave (8.b)
  public String postString() {
    StringJoiner sj = new StringJoiner(", ", "[", "]");
    Node<T> p = rot;

    if(p == null) return "[]";

    while(true) {
      if(p.venstre == p.høyre) { // Bladnode
        Node<T> q = p.forelder;

        sj.add(p.verdi.toString());

        while(q != null && (q.høyre == p || q.høyre == null)) {
          p = q;
          q = q.forelder;
          sj.add(p.verdi.toString());
        }

        if(q == null)
          break;

        p = q.høyre;
      } else { // Indrenode
        while(p.venstre != null) p = p.venstre;
        if(p.høyre != null) p = p.høyre;
      }
    }

    return sj.toString();
  }


  //Oppgave 9:
  @Override
  public Iterator<T> iterator() {
    return new BladnodeIterator();
  }

  private class BladnodeIterator implements Iterator<T> {
    private Node<T>
            p = rot,
            q = null;
    private boolean removeOK = false;
    private int iteratorendringer = endringer;

    private BladnodeIterator() // konstrukt�r
    {
      if(p == null) return;

      while(p.venstre != null || p.høyre != null) {
        while(p.venstre != null) p = p.venstre;
        if(p.høyre != null) p = p.høyre;
      }
    }

    @Override
    public boolean hasNext() {
      return p != null; // Denne skal ikke endres!
    }

    @Override
    public T next() {
      if(iteratorendringer != endringer)
        throw new ConcurrentModificationException("Treets struktur endret før metodekall!");

      if(p == null)
        throw new NoSuchElementException("Ingen bladnode funnet!");

      q = p;

      Node<T> r = p.forelder;

      while(r != null && (r.høyre == p || r.høyre == null)) {
        p = r;
        r = r.forelder;
      }

      if(r != null) {
        p = r.høyre;

        while(p.venstre != null || p.høyre != null) {
          while(p.venstre != null) p = p.venstre;
          if(p.høyre != null) p = p.høyre;
        }
      } else
        p = null;

      removeOK = true;

      return q.verdi;
    }

    //Oppgave 10

    @Override
    public void remove() {
      if(iteratorendringer != endringer)
        throw new ConcurrentModificationException("Treets struktur endret før metodekall!");

      if(!removeOK)
        throw new IllegalStateException("Operasjon kan ikke utføres før et kall på next()!");

      removeOK = false;

      Node<T> r = q.forelder;

      if(r != null) {
        if(r.venstre == q)
          r.venstre = null;
        else
          r.høyre = null;
      } else
        rot = null;

      q.forelder = null;
      q.verdi = null;
      q = null;

      antall--;
      endringer++;
      iteratorendringer++;
    }

  } // BladnodeIterator

} // ObligSBinTre
