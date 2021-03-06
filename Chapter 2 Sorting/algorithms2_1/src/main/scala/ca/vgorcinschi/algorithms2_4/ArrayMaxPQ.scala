package ca.vgorcinschi.algorithms2_4

import scala.annotation.tailrec
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  *
  * @param capacity - defaults to 10
  * @tparam Key - must be comparable
  */
class ArrayMaxPQ[Key](val capacity: Int = 10)(implicit tag: ClassTag[Key],
                                              override protected val cmp: Ordering[_ >: Key])
  extends MaxPQ[Key] {

  private implicit def optToKey(maybeKey: Option[Key]): Key = maybeKey.get

  protected var priorityQueue: Array[Option[Key]] = Array.fill(capacity)(None)

  //insert a key into a Priority Queue
  def insert(v: Key): Unit = {
    N += 1
    priorityQueue(N) = Some(v)
    swim(N)
  }

  //return and remove the largest key
  def delMax(): Key = {
    val maxValue = max()
    exch(1, N)
    N -= 1
    priorityQueue(N + 1) = None //avoid loitering
    sink(1)
    maxValue
  }

  //return the largest key
  def max(): Key = priorityQueue(1)

  protected def swim(key: Int): Unit = {
    @tailrec
    def innerLoop(k: Int): Unit = k match {
      case _ if k > 1 && less(k / 2, k) =>
        exch(k / 2, k)
        innerLoop(k / 2)
      case _ => //exit
    }

    innerLoop(key)
  }

  protected def sink(key: Int): Unit = {
    @tailrec
    def innerLoop(currentIndex: Int): Unit = 2 * currentIndex match {
      case left if left <= N =>
        val largestValueIndex = if (left < N && less(left, left + 1)) {
          left + 1
        } else left
        if (greater(currentIndex, largestValueIndex)) return
        exch(currentIndex, largestValueIndex)
        innerLoop(largestValueIndex)
      case _ =>
    }

    innerLoop(key)
  }

  protected def less(i: Int, j: Int): Boolean = cmp.lt(priorityQueue(i), priorityQueue(j))

  protected def greater: (Int, Int) => Boolean = (a: Int, b: Int) => !less(a, b)

  protected def exch(i: Int, j: Int): Unit = {
    val temp = priorityQueue(i)
    priorityQueue(i) = priorityQueue(j)
    priorityQueue(j) = temp
  }
}
