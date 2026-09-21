#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

REPO="p67906265-spec/manutenzione-Moto"
BACKUP_DIR="$HOME/storage/shared/Download/Firma_Manutenzione_Moto"
KEYSTORE="$BACKUP_DIR/paolo-release.jks"
PASSWORD_FILE="$BACKUP_DIR/CREDENZIALI_FIRMA.txt"
KEY_ALIAS_VALUE="paolo-manutenzione-moto"

pkg install gh openjdk-21 openssl coreutils -y
mkdir -p "$BACKUP_DIR"

if ! gh auth status >/dev/null 2>&1; then
  echo "Accedi a GitHub seguendo le indicazioni mostrate."
  gh auth login
fi

if [ -e "$KEYSTORE" ]; then
  echo "Esiste gia una chiave in $KEYSTORE: operazione interrotta per non sostituirla."
  exit 1
fi

KEYSTORE_PASSWORD_VALUE="$(openssl rand -hex 16)"
KEY_PASSWORD_VALUE="$(openssl rand -hex 16)"

keytool -genkeypair \
  -keystore "$KEYSTORE" \
  -storetype JKS \
  -storepass "$KEYSTORE_PASSWORD_VALUE" \
  -keypass "$KEY_PASSWORD_VALUE" \
  -alias "$KEY_ALIAS_VALUE" \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000 \
  -dname "CN=Paolo Free, OU=Manutenzione Moto, O=Paolo Free, C=IT"

base64 -w 0 "$KEYSTORE" | gh secret set SIGNING_KEY --repo "$REPO"
printf '%s' "$KEYSTORE_PASSWORD_VALUE" | gh secret set KEYSTORE_PASSWORD --repo "$REPO"
printf '%s' "$KEY_ALIAS_VALUE" | gh secret set KEY_ALIAS --repo "$REPO"
printf '%s' "$KEY_PASSWORD_VALUE" | gh secret set KEY_PASSWORD --repo "$REPO"

umask 077
printf 'KEYSTORE_PASSWORD=%s\nKEY_ALIAS=%s\nKEY_PASSWORD=%s\n' \
  "$KEYSTORE_PASSWORD_VALUE" "$KEY_ALIAS_VALUE" "$KEY_PASSWORD_VALUE" > "$PASSWORD_FILE"

echo
echo "Firma permanente configurata nei quattro GitHub Secrets."
echo "Conserva per sempre e in un luogo privato questa cartella:"
echo "$BACKUP_DIR"
