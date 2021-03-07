package com.lcampos.chrome

import com.lcampos.chrome.activetab.ActiveTabConfig

/**
 * This is the global config, which includes any configurable details.
 *
 * For convenience, there are two configs, the Default one and the one for Development.
 */
case class Config(
    activeTabConfig: activetab.ActiveTabConfig
)

object Config {

  val Default: Config = {
    Config(
      ActiveTabConfig()
    )
  }

  val Dev: Config = {
    Config(
      ActiveTabConfig()
    )
  }
}
