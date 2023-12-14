package service.model

import scala.collection.immutable

import enumeratum._

sealed trait TransactionStatus extends EnumEntry
object TransactionStatus extends Enum[TransactionStatus] with PlayJsonEnum[TransactionStatus] {

  override val values: immutable.IndexedSeq[TransactionStatus] = findValues

  case object PENDING   extends TransactionStatus
  case object COMPLETED extends TransactionStatus
  case object FAILED    extends TransactionStatus
}
