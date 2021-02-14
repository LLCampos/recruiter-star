package com.lcampos.chrome

import com.lcampos.chrome.activetab.ActiveTabConfig
import com.lcampos.chrome.background.alarms.AlarmRunner

/**
 * This is the global config, which includes any configurable details.
 *
 * For convenience, there are two configs, the Default one and the one for Development.
 */
case class Config(
    alarmRunnerConfig: AlarmRunner.Config,
    activeTabConfig: activetab.ActiveTabConfig
)

// TODO: REPLACE ME
object Config {

  val Default: Config = {
    Config(
      AlarmRunner.Config(periodInMinutes = 60 * 3),
      ActiveTabConfig()
    )
  }

  val Dev: Config = {
    Config(
      AlarmRunner.Config(periodInMinutes = 2),
      ActiveTabConfig()
    )
  }
}
