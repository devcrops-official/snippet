```shell
bash keystore_copy.sh [-v] --srckeystore mysourcestore.jks --destkeystore mydestkeystore.jks [OPTION]

where:
    --srckeystore  is the path to source keystore. Example : --srckeystore ./mysourcekeystore.jks 
    --destkeystore  is the path to keystore. Example : --destkeystore ./mydestkeystore.jks

option:
    --srckeystorepass  source keystore password (default password : weblogic1 ) 
    --srckeystorekeypass  source entry password (default password : weblogic1 ) 
    --destkeystorepass  destination keystore password (default password : weblogic1 )  
    --destkeystorekeypass  destination entry password (default password : weblogic1 )
    --aliasexclusion array of alias exclusions (example: --aliasexclusion demoidentity,democert )
    --serveralias is the alias server of new keystore, this option force the generation of new keystore
    -h  show this help text
    -v  to show verbose log
```

```
#!/bin/bash -x

usage="$(basename "$0") [-v] --srckeystore mysourcestore.jks --destkeystore mydestkeystore.jks [OPTION]

where:
    --srckeystore  is the path to source keystore. Example : --srckeystore ./mysourcekeystore.jks
    --destkeystore  is the path to keystore. Example : --destkeystore ./mydestkeystore.jks

option:
    --srckeystorepass  source keystore password (default password : weblogic1 )
    --srckeystorekeypass  source entry password (default password : weblogic1 )
    --destkeystorepass  destination keystore password (default password : weblogic1 )
    --destkeystorekeypass  destination entry password (default password : weblogic1 )
    --aliasexclusion array of alias exclusions (example: --aliasexclusion demoidentity,democert )
    --serveralias is the alias server of new keystore, this option force the generation of new keystore
    -h  show this help text
    -v  to show verbose log
    "

if (($# == 0)); then
    echo "$usage" >&2
    exit
fi

# set verbose level default to info
__VERBOSE=6

red=`tput setaf 1`
green=`tput setaf 2`
yellow=`tput setaf 3`
cyan=`tput setaf 6`
white=`tput setaf 7`
reset=`tput sgr0`

declare -A LOG_LEVELS
# https://en.wikipedia.org/wiki/Syslog#Severity_level
LOG_LEVELS=([0]="${red}EMERG${reset}" [1]="${red}ALERT${reset}" [2]="${red}CRIT${reset}" [3]="${red}ERROR${reset}" [4]="${red}WARNING${reset}" [5]="${yellow}NOTICE${reset}" [6]="${green}INFO${reset}" [7]="${white}DEBUG${reset}")
function log () {
  local LEVEL=${1}
  shift
  if [ ${__VERBOSE} -ge ${LEVEL} ]; then
    echo "[${LOG_LEVELS[$LEVEL]}]" "$@"
  fi
}



KEYSTORE_SRC_STOREPASS="weblogic1"
KEYSTORE_SRC_KEYPASS="weblogic1"
KEYSTORE_DEST_STOREPASS="weblogic1"
KEYSTORE_DEST_KEYPASS="weblogic1"

while getopts ':hv-:' optchar; do

  case "$optchar" in
    -) case "${OPTARG}" in
                srckeystore)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    log 7 "Parsing option: '--${OPTARG}', value: '${val}'"
                    if [ "$val" == "" ]; then
                        log 3 "illegal value for option: $OPTARG"
                        echo "$usage" >&2
                        exit
                    fi
                    KEYSTORE_SRC=${val}
                    ;;
                srckeystorepass)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    log 7 "Parsing option: '--${OPTARG}', value: '${val}'"
                    if [ "$val" == "" ]; then
                        log 3 "illegal value for option: $OPTARG"
                        echo "$usage" >&2
                        exit
                    fi
                    KEYSTORE_SRC_STOREPASS=${val}
                    ;;
                srckeystorekeypass)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    log 7 "Parsing option: '--${OPTARG}', value: '${val}'"
                    if [ "$val" == "" ]; then
                        log 3 "illegal value for option: $OPTARG"
                        echo "$usage" >&2
                        exit
                    fi
                    KEYSTORE_SRC_KEYPASS=${val}
                    ;;
                destkeystore)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    log 7 "Parsing option: '--${OPTARG}', value: '${val}'"
                    if [ "$val" == "" ]; then
                        log 3 "illegal value for option: $OPTARG"
                        echo "$usage" >&2
                        exit
                    fi
                    KEYSTORE_DEST=${val}
                    ;;
                destkeystorepass)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    log 7 "Parsing option: '--${OPTARG}', value: '${val}'"
                    if [ "$val" == "" ]; then
                        log 3 "illegal value for option: $OPTARG"
                        echo "$usage" >&2
                        exit
                    fi
                    KEYSTORE_DEST_STOREPASS=${val}
                    ;;
                destkeystorekeypass)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    log 7 "Parsing option: '--${OPTARG}', value: '${val}'"
                    if [ "$val" == "" ]; then
                        log 3 "illegal value for option: $OPTARG"
                        echo "$usage" >&2
                        exit
                    fi
                    KEYSTORE_DEST_KEYPASS=${val}
                    ;;
                aliasexclusion)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    log 7 "Parsing option: '--${OPTARG}', value: '${val}'"
                    if [ "$val" == "" ]; then
                        log 3 "illegal value for option: $OPTARG"
                        echo "$usage" >&2
                        exit
                    fi
                    ALIAS_EXCLUSIONS=${val}
                    ;;
                serveralias)
                    val="${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ))
                    log 7 "Parsing option: '--${OPTARG}', value: '${val}'"
                    if [ "$val" == "" ]; then
                        log 3 "illegal value for option: $OPTARG"
                        echo "$usage" >&2
                        exit
                    fi
                    SERVER_ALIAS=${val}
                    ;;
                *)
                    if [ "$OPTERR" = 1 ] && [ "${optspec:0:1}" != ":" ]; then
                        log 3 "Unknown option --${OPTARG}"
                        echo "$usage" >&2
                        exit
                    fi
                    ;;
            esac;;
    h) echo "$usage" >&2
       exit
       ;;
    v) __VERBOSE=7
       ;;
    :) log 3 "missing argument for -%s\n" "$OPTARG"
       echo "$usage" >&2
       exit 1
       ;;
   \?) log 3 "illegal option: -%s\n" "$OPTARG"
       echo "$usage" >&2
       exit 1
       ;;
  esac
done

IFS=',' read -r -a ALIAS_EXCLUSIONS_ARRAY <<< "$ALIAS_EXCLUSIONS"
log 6 "SOURCE KEYSTORE      = ${KEYSTORE_SRC}"
log 6 "DESTINATION KEYSTORE = ${KEYSTORE_DEST}"
log 6 "ALIAS EXCLUSIONS     = ${ALIAS_EXCLUSIONS}"

## Generation of new keystore
if [ "$SERVER_ALIAS" != "" ]; then
    if error=`keytool -genkey -alias ${SERVER_ALIAS} -keyalg RSA -keypass ${KEYSTORE_DEST_KEYPASS} -storepass ${KEYSTORE_DEST_STOREPASS} -keystore ${KEYSTORE_DEST} -dname 'CN='$SERVER_ALIAS', OU=Divisione Sistemi - Devops & Engineering, O=Banca Mediolanum S.p.a., L=Basiglio, ST=Milano, C=IT' ` ; then
      log 6 "Generated new keystore in ${KEYSTORE_DEST} "
    else
      log 3 "Error generating new keystore with alias ${SERVER_ALIAS} : ${error} "
      exit
    fi
fi

keytool -list -v -keystore ${KEYSTORE_SRC} -storepass ${KEYSTORE_SRC_STOREPASS} | grep -Po "Alias\sname:\s\K(.*)" | while read -r alias_id ; do
  ALIAS_LABEL="${cyan}${alias_id}${reset}"

  ## Check if ALIAS belongs to exclusions
  EXCLUDED=0
  for element in "${ALIAS_EXCLUSIONS_ARRAY[@]}"
  do
    [[ $element =~ (^|[[:space:]])$alias_id($|[[:space:]]) ]] && EXCLUDED=1
  done
  if [ "$EXCLUDED" == 1 ]; then
    log 5 "The certificate associated with alias  ${ALIAS_LABEL} will not be imported because belongs to exclusions. "
    continue
  else
    log 7 "The certificate associated with alias  ${ALIAS_LABEL} doesn't belong to exclusions. "
  fi

  ## Delete ALIAS if exist in destination keystore
  if keytool -list -keystore ${KEYSTORE_DEST} -alias ${alias_id} -storepass ${KEYSTORE_DEST_STOREPASS} | grep "Certificate" --quiet ; then
    log 5 "The certificate associated with alias ${ALIAS_LABEL} is already present in the keystore [${KEYSTORE_DEST}], will be deleted. "
    keytool -delete -alias ${alias_id} -keystore ${KEYSTORE_DEST} -storepass ${KEYSTORE_DEST_STOREPASS}
  fi

  ## Import ALIAS in destination keystore
  log 6 "Import the certificate associated withe alias ${ALIAS_LABEL} in the keystore [${KEYSTORE_DEST}] ... "
  if keytool -list -keystore  ${KEYSTORE_SRC} -alias ${alias_id} -storepass ${KEYSTORE_SRC_STOREPASS} | grep PrivateKeyEntry --quiet; then
    log 7 "The alias ${ALIAS_LABEL} is protected by keypass, will be protected in destination keystore too."
    if error=`keytool -importkeystore -srckeystore ${KEYSTORE_SRC} -destkeystore ${KEYSTORE_DEST} -srcalias ${alias_id} -srcstorepass ${KEYSTORE_SRC_STOREPASS} -deststorepass ${KEYSTORE_DEST_STOREPASS} -srckeypass ${KEYSTORE_SRC_KEYPASS} -destkeypass ${KEYSTORE_DEST_KEYPASS}` ; then
        log 6 "The certificate associated with alias ${ALIAS_LABEL} has been imported correctly in destination keystore [${KEYSTORE_DEST}]"
    else
        log 3 "Error importing certificate associated with alias ${ALIAS_LABEL} : ${error} "
    fi
  else
    if error=`keytool -importkeystore -srckeystore ${KEYSTORE_SRC} -destkeystore ${KEYSTORE_DEST} -srcalias ${alias_id} -srcstorepass ${KEYSTORE_SRC_STOREPASS} -deststorepass ${KEYSTORE_DEST_STOREPASS}` ; then
        log 6 "The certificate associated with alias ${ALIAS_LABEL} has been imported correctly in destination keystore [${KEYSTORE_DEST}]"
    else
        log 3 "Error importing certificate associated with alias ${ALIAS_LABEL} : ${error} "
    fi
  fi
done
```
