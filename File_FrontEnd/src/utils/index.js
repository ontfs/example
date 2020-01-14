const CryptoJS = require('crypto-js') //

const key = CryptoJS.enc.Utf8.parse('1992101219960210') //
const iv = CryptoJS.enc.Utf8.parse('1996021019921012') //

//
function Decrypt(word) {
  let encryptedHexStr = CryptoJS.enc.Hex.parse(word)
  let srcs = CryptoJS.enc.Base64.stringify(encryptedHexStr)
  let decrypt = CryptoJS.AES.decrypt(srcs, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  })
  let decryptedStr = decrypt.toString(CryptoJS.enc.Utf8)
  return decryptedStr.toString()
}

//
function Encrypt(word) {
  let srcs = CryptoJS.enc.Utf8.parse(word)
  let encrypted = CryptoJS.AES.encrypt(srcs, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  })
  return encrypted.ciphertext.toString().toUpperCase()
}

function getUTCTime(inputTime) {
  let date = new Date(inputTime)
  let Y = date.getUTCFullYear()
  let M =
    date.getUTCMonth() + 1 < 10
      ? '0' + (date.getUTCMonth() + 1)
      : date.getUTCMonth() + 1
  let mouth = ''

  switch (M.toString()) {
    case '01':
      mouth = 'Jan-'
      break
    case '02':
      mouth = 'Feb-'
      break
    case '03':
      mouth = 'Mar-'
      break
    case '04':
      mouth = 'Apr-'
      break
    case '05':
      mouth = 'May-'
      break
    case '06':
      mouth = 'Jun-'
      break
    case '07':
      mouth = 'Jul-'
      break
    case '08':
      mouth = 'Aug-'
      break
    case '09':
      mouth = 'Sep-'
      break
    case '10':
      mouth = 'Oct-'
      break
    case '11':
      mouth = 'Nov-'
      break
    case '12':
      mouth = 'Dec-'
      break
    default:
      break
  }

  let D =
    date.getUTCDate() < 10
      ? '0' + date.getUTCDate() + '-'
      : date.getUTCDate() + '-'
  let h =
    date.getUTCHours() < 10 ? '0' + date.getUTCHours() : date.getUTCHours()
  let m =
    date.getUTCMinutes() < 10
      ? '0' + date.getUTCMinutes()
      : date.getUTCMinutes()
  let s =
    date.getUTCSeconds() < 10
      ? '0' + date.getUTCSeconds()
      : date.getUTCSeconds()

  return mouth + D + Y + ' ' + h + ':' + m + ':' + s + ' UTC'
}
function getTransDate(inputTime) {
  inputTime = inputTime * 1000
  getUTCTime(inputTime)
}
const HelperTools = {
  getDateTime(inputTime) {
    let date = new Date(inputTime * 1000)
    let NowTime = new Date()
    let showtime = (NowTime - date) / 1000

    return parseInt(showtime)
  },

  getShowDate(inputTime) {
    if (inputTime <= 60) {
      return inputTime + 's'
    }
    if (60 < inputTime && inputTime <= 3600) {
      return parseInt(inputTime / 60) + 'm'
    }
    if (3600 < inputTime && inputTime <= 86400) {
      return parseInt(inputTime / 3600) + 'h'
    }
    if (inputTime > 86400) {
      return parseInt(inputTime / 86400) + 'd'
    }
  },

  getTransDate(inputTime) {
    inputTime = inputTime * 1000

    if (window.localStorage.getItem('user_lang') === 'zh') {
      return this.getPRCTime(inputTime)
    } else {
      return this.getUTCTime(inputTime)
    }
  },

  /**
   *
   *
   * @param inputTime
   * @return {string}
   */
  getUTCTime(inputTime) {
    let date = new Date(inputTime)
    let Y = date.getUTCFullYear()
    let M =
      date.getUTCMonth() + 1 < 10
        ? '0' + (date.getUTCMonth() + 1)
        : date.getUTCMonth() + 1
    let mouth = ''

    switch (M.toString()) {
      case '01':
        mouth = 'Jan-'
        break
      case '02':
        mouth = 'Feb-'
        break
      case '03':
        mouth = 'Mar-'
        break
      case '04':
        mouth = 'Apr-'
        break
      case '05':
        mouth = 'May-'
        break
      case '06':
        mouth = 'Jun-'
        break
      case '07':
        mouth = 'Jul-'
        break
      case '08':
        mouth = 'Aug-'
        break
      case '09':
        mouth = 'Sep-'
        break
      case '10':
        mouth = 'Oct-'
        break
      case '11':
        mouth = 'Nov-'
        break
      case '12':
        mouth = 'Dec-'
        break
      default:
        break
    }

    let D =
      date.getUTCDate() < 10
        ? '0' + date.getUTCDate() + '-'
        : date.getUTCDate() + '-'
    let h =
      date.getUTCHours() < 10 ? '0' + date.getUTCHours() : date.getUTCHours()
    let m =
      date.getUTCMinutes() < 10
        ? '0' + date.getUTCMinutes()
        : date.getUTCMinutes()
    let s =
      date.getUTCSeconds() < 10
        ? '0' + date.getUTCSeconds()
        : date.getUTCSeconds()

    return mouth + D + Y + ' ' + h + ':' + m + ':' + s + ' UTC'
  },

  /**
   *
   *
   * @param inputTime
   * @return {string}
   */
  getPRCTime(inputTime) {
    let date = new Date(inputTime)
    let y = date.getFullYear()
    let m = date.getMonth() + 1
    m = m < 10 ? '0' + m : m
    let d = date.getDate()
    d = d < 10 ? '0' + d : d
    let h = date.getHours()
    h = h < 10 ? '0' + h : h
    let minute = date.getMinutes()
    let second = date.getSeconds()
    minute = minute < 10 ? '0' + minute : minute
    second = second < 10 ? '0' + second : second
    return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second
  },

  getDayFunc(second_time) {
    let time = parseInt(second_time)

    if (parseInt(second_time) > 60) {
      let second = parseInt(second_time) % 60
      let min = parseInt(second_time / 60)
      time = min + ':' + second

      if (min > 60) {
        min = parseInt(second_time / 60) % 60
        let hour = parseInt(parseInt(second_time / 60) / 60)
        time = hour + ':' + min + ':' + second

        if (hour > 24) {
          hour = parseInt(parseInt(second_time / 60) / 60) % 24
          let day = parseInt(parseInt(parseInt(second_time / 60) / 60) / 24)
          time = day + 'day' + hour + ':' + min + ':' + second
        }
      }
    }
    return time
  }
}
export default {
  Decrypt,
  Encrypt,
  HelperTools
}
