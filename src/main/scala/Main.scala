import zio.*
import zio.direct.*
import zio.stream.*

import java.io.File
import java.text.DecimalFormat

case class Reading(station: String, temp: Double)

case class Station(min: Double, max: Double, total: Double, num: Int):
  override def toString: String =
    val mean = total / num
    s"$min/${meanFormat.format(mean)}/$max"

val meanFormat = DecimalFormat("#.#")

val readReading = ZPipeline.utf8Decode >>> ZPipeline.splitLines.map: line =>
  val Array(station, temp) = line.split(';')
  Reading(station, temp.toDouble)

def updateStation(data: Map[String, Station], reading: Reading) =
  data.updatedWith(reading.station): maybeData =>
    Some:
      maybeData.fold(Station(reading.temp, reading.temp, reading.temp, 1)): data =>
        val newMin = Math.min(reading.temp, data.min)
        val newMax = Math.max(reading.temp, data.max)
        val newTotal = data.total + reading.temp
        val newNum = data.num + 1
        data.copy(newMin, newMax, newTotal, newNum)

object Main extends ZIOAppDefault:
  override def run =
    defer:
      val args = getArgs.run
      val summary = ZStream.fromFile(File(args.head))
        .via(readReading)
        .runFold(Map.empty)(updateStation)
        .run

      val strings = summary.toList.sortBy(_._1).map: data =>
        s"${data._1}=${data._2}"

      Console.printLine(s"{${strings.mkString(", ")}}").run
