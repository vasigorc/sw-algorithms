package ca.vgorcinschi.algorithms2_4

import ca.vgorcinschi.BaseSpec

import scala.util.Random

class MutableLinkedPQSpec extends BaseSpec {

  trait IntPQBuilder {
    val instance = new MutableLinkedPQ[Int] with PQMetricsCollector[Int]
  }

  behavior of "delMax"

  it should "return the highest value" in new IntPQBuilder {
    // generate a list of random inputs
    val randomInput: List[Int] = randomIntList(listLength = 100)

    // populate pq
    randomInput.foreach(instance.insert)

    // check that both highest value is removed every time and that the size is decreased by one
    randomInput.sorted.reverse.foreach(n => n shouldEqual instance.delMax())
  }

  private def randomIntList(listLength: Int = 1, upperBound: Int = 1000): List[Int] = {
    (1 to listLength map (_ => Random.nextInt(upperBound))).toList
  }

  it should "decrease size by one" in new IntPQBuilder {
    // insert two items
    randomIntList(listLength = 2).foreach(instance.insert)

    // remove one item
    instance.delMax()

    // expected size should be 1
    instance.size() shouldEqual  1
  }

  it should "throw NullPointerException when PQ is empty" in new IntPQBuilder {
    the [NullPointerException] thrownBy instance.delMax()
  }

  // this should be moved into a property based class
  it should "guarantee logarithmic running time" in new IntPQBuilder {
    1 to 10 foreach { _ =>
      instance.insert(Random.nextInt(1000))
    }

    val sizeBeforeDelete = instance.size()
    instance.delMax()
    instance.deleteStepsCounter should be <= math.max(1, (sizeBeforeDelete / 2) - 1) * math.log(sizeBeforeDelete).toInt
  }

  behavior of "max"

  it should "return the highest value and leave size intact" in new IntPQBuilder {
    val randomInts: List[Int] = randomIntList(listLength = 100)
    randomInts foreach instance.insert

    instance.max() shouldEqual randomInts.sorted.reverse.head
  }

  it should "throw IllegalArgumentException when PQ is empty"

  behavior of "insert"

  it should "increase size by one" in new IntPQBuilder {
    instance.insert(42)
    instance.size() shouldEqual 1
  }

  // to be moved into a property based test class
  it should "guarantee logarithmic running time" in new IntPQBuilder {
    1 to 10 foreach { _ =>
      instance.insert(Random.nextInt(1000))
    }
    instance.insert(Random.nextInt(1000))

    instance.insertStepsCounter should be <= math.max(1, (instance.size() / 2) - 1) * math.log(instance.size()).toInt
  }
}

/**
  * Attention! This hook is not designed to be thread-safe if the same
  * [[MutableLinkedPQ]] object is accessed by multiple threads
  *
  * @tparam Key
  */
trait PQMetricsCollector[Key] extends MutableLinkedPQ[Key] {

  var insertStepsCounter: Int = 0
  var deleteStepsCounter: Int = 0

  // insert part
  override def insert(value: Key): Unit = {
    insertStepsCounter = 0
    super.insert(value)
  }

  override protected def insertHelper(nextTree: Tree[Key], value: Key): Branch[Key] = {
    insertStepsCounter +=1
    super.insertHelper(nextTree, value)
  }

  override def swim[U <: Tree[Key]](tree: U): Tree[Key] = {
    insertStepsCounter += 1
    super.swim(tree)
  }

  // delMax part

  override def delMax(): Key = {
    deleteStepsCounter = 0
    // reset counter on every call
    super.delMax()
  }

  override def removeLastBranch(currentTree: Tree[T]): (Tree[T], T) = {
    deleteStepsCounter += 1
    super.removeLastBranch(currentTree)
  }

  override def sink(branch: Tree[T]): Unit = {
    deleteStepsCounter += 1
    super.sink(branch)
  }
}