/*********************************************
**Author:  Jessica Byrd
**Date:    11/13/2019
**Purpose: Bounded Buffer/Synchronized Class
**         My Synchronized Class/Methods
**         Buffer/Producer
**	       Class Buffer->
**		   Objection: To prevent thread interference
**		   During data transmission
**		   Thread Synchronization
**         Using Mutual Exclusive
**		   Synchronized method, block and static
**Version: 1.0
*/

//Imports
import java.util.Random;

/*********************BUFFER*********************/
class Buffer
{

    private int[] buf;
    private int in = 0;
    private int out= 0;
    private int count = 0;
    private int size;

    Buffer(int size)
    {
        this.size = size;
        buf = new int[size];
    }

    synchronized public void put(int o)
    {
        while (count==size)
        {
            System.out.println(">>> PUT: Buffer Full, blocking");
            try {wait();} catch(InterruptedException e){}
        }

        //Add an item to the buffer
        buf[in] = o;
        ++count;
        in=(in+1) % size;
        notifyAll();
    }

    synchronized public int get()
    {
        while (count==0)
        {
            System.out.println("<<< GET: Buffer empty, blocking");
            try {wait();} catch(InterruptedException e){}
        }

        //Remove an item from the buffer
        int ret_val = buf[out];
        --count;
        out=(out+1) % size;
        notifyAll();
        return (ret_val);
       }

}

//End class Buffer

/*******************PRODUCER************************/

class Producer extends Thread
{

    Random bubba = new Random();
    public boolean stop = false;
    String name;
    int value = 0;

    Buffer buf_;

    Producer(Buffer b, String n, int start)
    {
        buf_ = b;
        name = n;
        value = start;
    }

    public void run()
    {
      try
      {
        while(!stop)
        {
			//value = bubba.nextInt(100);
			value += 1;

            //Producer Work
            Thread.sleep(600);
            System.out.println(name + ", Putting value: " + value);
            buf_.put(value);
        }
      }

      catch (InterruptedException e)
      {
        System.out.println("Producer: But I wasn't done!!!");
      }
    }
}
//End class Producer


/****************************Middleman Class***************************/

class Middleman extends Thread
{
	//Buffer1
	Buffer buf_;

	//Buffer2
	Buffer buffer2;

	String name;
	public boolean stop = false;



    Middleman(Buffer b, Buffer b2, String n)
    {
        buf_ = b;
        buffer2 = b2;
        name = n;

    }

    public void run()
    {
		//System.out.println(name +", ------>**Value: ");
      try
      {
		  int value;

		  //System.out.println(name +", ------>**Value: ");

		//Work
		Thread.sleep(1000);

		//System.out.println("Going: " + name);

        while(!stop)
        {


			value = buf_.get();
			System.out.println(name +", ------>**Value: " + value);
			value += 100;
			buffer2.put(value);


            System.out.println(name + ", ---------> *Value: " + value);

            Thread.sleep(1500);
        }
      }

      catch (InterruptedException e)
      {
        System.out.println("Middleman at work taking from the producer and sending data
        					back to the consumer");
      }
    }
}

/********************CONSUMER*******************************/

class Consumer extends Thread
{
    Buffer buf_;
    String name;
    public boolean stop = false;


    Consumer(Buffer b, String n)
    {
        buf_ = b;
        name = n;
    }

    public void run()
    {
        try
        {
          int value;

          //Consume Work
          Thread.sleep(1400);
          while(!stop)
          {
            value = buf_.get();
            System.out.println("      " + name + ", Obtained Value: " + value);

            //Consume Work
            Thread.sleep(800);
          }
        }

        catch (InterruptedException e)
        {
          System.out.println("Consumer: But I wasn't done!!!");
        }
    }
}

//End class Consumer



/*****************************Bounded Buffer***************************/

public class BoundedBuffer
{

   public static void main (String args[])
   {
	  //Creating a new buffer queue
	  //Buffer one has a size of 10
      Buffer Buff = new Buffer(10);

      //Creating a new buffer queue
      //Buffer two has a size of 10
      Buffer Buffer2 = new Buffer(10);

      //Starting a new Producer Thread
      Producer Prod = new Producer(Buff,"Producer 1",0);

      //Starting a Middleman Thread
      Middleman Mid = new Middleman(Buff, Buffer2, "Middleman 1");

      //Starting a new Consumer Thread
      Consumer Cons = new Consumer(Buffer2,"Consumer 1");

      System.out.println("Please work now****************************");

	  //Try block
      try
      {
		//Start each
        Prod.start();
        Mid.start();
        Cons.start();

        //Thread sleep
        Thread.sleep(30000);

        System.out.println("Main: I'm tired of waiting!");

        //Stop each
        Prod.stop = true;
        Mid.stop = true;
        Cons.stop = true;
      }

	  //Catch
      catch (InterruptedException e) {}

   }
}
//End of the program
