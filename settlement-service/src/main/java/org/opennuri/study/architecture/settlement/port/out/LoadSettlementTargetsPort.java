package org.opennuri.study.architecture.settlement.port.out;

import java.util.List;
import org.opennuri.study.architecture.settlement.domain.SettlementItem;

public interface LoadSettlementTargetsPort {
    List<SettlementItem> loadTargets();
}
