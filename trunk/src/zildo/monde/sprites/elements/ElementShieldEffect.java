package zildo.monde.sprites.elements;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.utils.CompositeElement;

public class ElementShieldEffect extends Element {

    public enum ShieldType {
        REDBALL;
    }

    float alpha = 0.0f;
    Element affected; // Shield is affected to this element
    ShieldType shieldType;
    CompositeElement composite;

    public ElementShieldEffect(Element p_linked, ShieldType p_shieldType) {
        affected = p_linked;
        shieldType = p_shieldType;
        switch (shieldType) {
            case REDBALL:
                setNSpr(ElementDescription.REDBALL3.ordinal());
                setNBank(SpriteBank.BANK_ELEMENTS);
                composite = new CompositeElement(this);
                composite.followShape();
                break;
        }
        x = affected.x;
        y = affected.y;
        z = affected.z;
    }

    @Override
    public void animate() {

        switch (shieldType) {
            case REDBALL:
                composite.animate();

                x = (int) (affected.x + 15 * Math.sin(alpha));
                y = (int) (affected.y + 10 * Math.cos(alpha));

                y -= getSprModel().getTaille_y() / 2;

                alpha += 0.06;
                break;
        }

        super.animate();
    }
}