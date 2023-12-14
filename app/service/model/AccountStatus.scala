package service.model

import scala.collection.immutable

import enumeratum._

sealed trait AccountStatus extends EnumEntry
object AccountStatus extends Enum[AccountStatus] with PlayJsonEnum[AccountStatus] {

  override val values: immutable.IndexedSeq[AccountStatus] = findValues

  case object ACTIVE  extends AccountStatus
  case object INACTIVE extends AccountStatus

}
